package kr.ac.knu.cse.global.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import kr.ac.knu.cse.token.domain.TokenType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
	private String secret;
	private String bearerType;
	private String authHeader;
	private Expiration expiration = new Expiration();

	@Getter @Setter
	public static class Expiration {
		private Integer access;
		private Integer refresh;
	}

	public Integer getExpiration(TokenType tokenType) {
		return tokenType == TokenType.ACCESS_TOKEN ? expiration.getAccess() : expiration.getRefresh();
	}
}
