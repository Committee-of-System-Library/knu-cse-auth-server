package kr.ac.knu.cse.token.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import kr.ac.knu.cse.client.domain.AuthClient;
import kr.ac.knu.cse.client.exception.AuthClientNotFoundException;
import kr.ac.knu.cse.client.persistence.AuthClientRepository;
import kr.ac.knu.cse.global.properties.JwtProperties;
import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.provider.exception.ProviderNotFoundException;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.token.domain.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenService {
    private final ProviderRepository providerRepository;
    private final AuthClientRepository authClientRepository;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;
    private SecretKey defaultSignKey;

    @PostConstruct
    public void init() {
        log.info("기본 JWT 서명 키 초기화");
        this.defaultSignKey = new SecretKeySpec(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
    }

    public Token generateToken(Authentication authentication) {
        log.info("기본 토큰 생성 - 사용자: {}", authentication.getName());
        return doGenerateToken(authentication, null);
    }

    public Token generateToken(Authentication authentication, Long clientId) {
        log.info("클라이언트용 토큰 생성 - 사용자: {}, 클라이언트 ID: {}", authentication.getName(), clientId);
        return doGenerateToken(authentication, clientId);
    }

    private Token doGenerateToken(Authentication jwtAuthentication, Long clientId) {
        SecretKey signKey = getSignKey(clientId);

        String token = Jwts.builder()
                .header().add(buildHeader()).and()
                .claims(buildPayload(jwtAuthentication, clientId))
                .expiration(buildExpiration(jwtProperties.getExpiration()))
                .signWith(signKey)
                .compact();

        log.info("토큰 생성 완료 - 사용자: {}", jwtAuthentication.getName());
        return new Token(jwtProperties.getBearerType(), token);
    }

    private Map<String, Object> buildHeader() {
        return Map.of(
                "typ", "JWT",
                "alg", "HS256",
                "regDate", System.currentTimeMillis()
        );
    }

    private Map<String, Object> buildPayload(Authentication authentication, Long clientId) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", authentication.getName());
        payload.put("role", principalDetails.student() != null ?
                principalDetails.student().getRole().name() : "GUEST");

        // studentId는 null일 수 있으므로 조건부로 추가
        if (principalDetails.student() != null) {
            payload.put("studentId", principalDetails.student().getStudentNumber());
        }

        if (clientId != null) {
            payload.put("clientId", clientId);
        }

        log.debug("JWT 페이로드 생성: {}", payload);
        return payload;
    }

    private Date buildExpiration(Integer expirationSeconds) {
        long expirationMillis = System.currentTimeMillis() + (expirationSeconds.longValue() * 1000L);
        return new Date(expirationMillis);
    }

    public Jws<Claims> extractClaims(String tokenValue) {
        Long clientId = null;
        try {
            String[] tokenParts = tokenValue.split("\\.");
            if (tokenParts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format");
            }

            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(tokenParts[1]));
            Map<String, Object> claimsMap = objectMapper.readValue(payloadJson, new TypeReference<>() {
            });

            Object clientIdObj = claimsMap.get("clientId");
            if (clientIdObj instanceof Number) {
                clientId = ((Number) clientIdObj).longValue();
            }
            log.debug("토큰에서 추출된 클라이언트 ID: {}", clientId);

        } catch (Exception e) {
            log.warn("토큰에서 clientId 추출 실패: {}", e.getMessage());
        }

        SecretKey signKey = getSignKey(clientId);

        try {
            log.debug("JWT 검증 시작 - 클라이언트 ID: {}", clientId);
            return Jwts.parser()
                    .verifyWith(signKey)
                    .build()
                    .parseSignedClaims(tokenValue);
        } catch (JwtException e) {
            log.error("JWT 검증 실패. 사용된 clientId: {}, 오류: {}", clientId, e.getMessage());
            throw e;
        }
    }


    public String extractEmail(Jws<Claims> claimsJws) {
        return claimsJws.getPayload().get("email", String.class);
    }

    public String extractRole(Jws<Claims> claimsJws) {
        return claimsJws.getPayload().get("role", String.class);
    }

    public String extractStudentId(Jws<Claims> claimsJws) {
        return claimsJws.getPayload().get("studentId", String.class);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            log.debug("Authorization 헤더에서 토큰 발견");
            return bearerToken.substring(7);
        }
        log.debug("Authorization 헤더에 토큰 없음");
        return null;

    }

    public Authentication getAuthentication(String email) {
        log.info("인증 객체 생성 - 이메일: {}", email);
        Provider provider = providerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Provider를 찾을 수 없습니다: {}", email);
                    return new ProviderNotFoundException();
                });
        PrincipalDetails principalDetails = PrincipalDetails.builder()
                .provider(provider)
                .student(provider.getStudent())
                .attributes(null)
                .build();
        log.info("인증 객체 생성 완료 - 이메일: {}", email);
        return new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());
    }

    public SecretKey getSignKey(Long clientId) {
        if (clientId == null) {
            log.debug("기본 서명 키 사용");
            return defaultSignKey;
        }

        log.debug("클라이언트별 서명 키 조회 - 클라이언트 ID: {}", clientId);
        return getSignKeyFromDb(clientId);
    }

    @Cacheable(value = "secretKey", key = "#clientId", unless = "#clientId == null")
    public SecretKey getSignKeyFromDb(Long clientId) {
        log.info("DB에서 클라이언트 서명 키 조회 - 클라이언트 ID: {}", clientId);
        AuthClient client = authClientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("클라이언트를 찾을 수 없습니다: ClientId={}", clientId);
                    return new AuthClientNotFoundException();
                });
        log.info("DB에서 클라이언트 정보 조회 완료: ID={}, Name={}", client.getClientId(), client.getClientName());

        return new SecretKeySpec(
                client.getJwtSecret().getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
    }
}
