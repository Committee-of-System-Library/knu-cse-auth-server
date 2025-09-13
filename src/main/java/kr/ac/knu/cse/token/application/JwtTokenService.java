package kr.ac.knu.cse.token.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import kr.ac.knu.cse.global.properties.JwtProperties;
import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.provider.exception.ProviderNotFoundException;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.token.domain.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
	private final JwtProperties jwtProperties;
	private SecretKey signKey;

	@PostConstruct
	public void init() {
		this.signKey = new SecretKeySpec(
			jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
			"HmacSHA256"
		);
	}

	public Token generateToken(Authentication authentication) {
		return doGenerateToken(authentication);
	}

	private Token doGenerateToken(Authentication jwtAuthentication) {
		String token = Jwts.builder()
			.header().add(buildHeader()).and()
			.claims(buildPayload(jwtAuthentication))
			.expiration(buildExpiration(jwtProperties.getExpiration()))
			.signWith(signKey)
			.compact();

		return new Token(jwtProperties.getBearerType(), token);
	}

	private Map<String, Object> buildHeader() {
		return Map.of(
			"typ", "JWT",
			"alg", "HS256",
			"regDate", System.currentTimeMillis()
		);
	}

	private Map<String, Object> buildPayload(Authentication authentication) {
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

		Map<String, Object> payload = new HashMap<>();
		payload.put("email", authentication.getName());
		payload.put("role", principalDetails.student() != null ?
			principalDetails.student().getRole().name() : "GUEST");

		// studentId는 null일 수 있으므로 조건부로 추가
		if (principalDetails.student() != null) {
			payload.put("studentId", principalDetails.student().getStudentNumber());
		}
		
		return payload;
	}

	private Date buildExpiration(Integer expirationSeconds) {
		long expirationMillis = System.currentTimeMillis() + (expirationSeconds.longValue() * 1000L);
		return new Date(expirationMillis);
	}

	public Jws<Claims> extractClaims(String tokenValue) {
		return Jwts.parser()
			.verifyWith(signKey)
			.build()
			.parseSignedClaims(tokenValue);
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
            return bearerToken.substring(7);
        }
        return null;

	}

	public Authentication getAuthentication(String email) {
		Provider provider = providerRepository.findByEmail(email)
			.orElseThrow(ProviderNotFoundException::new);
		PrincipalDetails principalDetails = PrincipalDetails.builder()
			.provider(provider)
			.student(provider.getStudent())
			.attributes(null)
			.build();
		log.info("[JwtTokenService] email : {}", email);
		return new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());
	}
}
