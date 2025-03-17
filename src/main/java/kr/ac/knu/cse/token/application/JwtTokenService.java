package kr.ac.knu.cse.token.application;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import kr.ac.knu.cse.global.properties.JwtProperties;
import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.provider.excpetion.ProviderNotFoundException;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.token.domain.Token;
import kr.ac.knu.cse.token.domain.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

	public Token generateToken(Authentication authentication, TokenType type) {
		if (type == TokenType.ACCESS_TOKEN) {
			return doGenerateToken(authentication, TokenType.ACCESS_TOKEN);
		} else if (type == TokenType.REFRESH_TOKEN) {
			return doGenerateToken(authentication, TokenType.REFRESH_TOKEN);
		}

		throw new IllegalArgumentException("Unsupported token type: " + type);
	}

	private Token doGenerateToken(Authentication jwtAuthentication, TokenType type) {
		String token = Jwts.builder()
			.header().add(buildHeader(type)).and()
			.claims(buildPayload(jwtAuthentication))
			.expiration(buildExpiration(jwtProperties.getExpiration(type)))
			.signWith(signKey)
			.compact();

		return new Token(jwtProperties.getBearerType(), type, token);
	}

	private Map<String, Object> buildHeader(TokenType type) {
		return Map.of(
			"typ", "JWT",
			"cat", type.name(),
			"alg", "HS256",
			"regDate", System.currentTimeMillis()
		);
	}

	private Map<String, Object> buildPayload(Authentication authentication) {
		return Map.of(
			"email", authentication.getName()
		);
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

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(jwtProperties.getAuthHeader());

		if (bearerToken != null && bearerToken.startsWith(jwtProperties.getBearerType())) {
			return bearerToken.replaceAll(jwtProperties.getBearerType(), "").trim();
		}

		return null;
	}

	public Authentication getAuthentication(String email) {
		Provider provider = providerRepository.findByEmail(email)
			.orElseThrow(ProviderNotFoundException::new);
		PrincipalDetails principalDetails = PrincipalDetails.builder()
			.provider(provider)
			.student(provider.getStudent())
			.build();

		return new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());
	}
}
