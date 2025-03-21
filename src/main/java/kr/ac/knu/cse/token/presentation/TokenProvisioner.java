package kr.ac.knu.cse.token.presentation;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.knu.cse.global.properties.JwtProperties;
import kr.ac.knu.cse.global.util.CookieGenerator;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.token.application.JwtTokenService;
import kr.ac.knu.cse.token.application.RefreshTokenService;
import kr.ac.knu.cse.token.domain.Token;
import kr.ac.knu.cse.token.domain.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvisioner {
	private final JwtTokenService jwtTokenService;
	private final RefreshTokenService refreshTokenService;
	private final CookieGenerator cookieGenerator;
	private final JwtProperties jwtProperties;

	public void tokenIssue(
		Authentication authentication,
		HttpServletResponse response
	) throws IOException {
		if (!(authentication.getPrincipal() instanceof PrincipalDetails principalDetails)) {
			log.error("Authentication Principal is not PrincipalDetails");
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Authentication error");
			return;
		}
		String email = principalDetails.getName();

		// Jwt 토큰 생성
		Token newAccessToken = jwtTokenService.generateToken(authentication, TokenType.ACCESS_TOKEN);
		Token newRefreshToken = jwtTokenService.generateToken(authentication, TokenType.REFRESH_TOKEN);
		refreshTokenService.updateRefreshToken(email, newRefreshToken.value());

		// 쿠키 생성
		Cookie accessCookie = cookieGenerator.generateCookie(
			"access_token",
			newAccessToken.value(),
			jwtProperties.getExpiration().getAccess()
		);

		Cookie refreshCookie = cookieGenerator.generateCookie(
			"refresh_token",
			newRefreshToken.value(),
			jwtProperties.getExpiration().getRefresh());

		// 쿠키 주입
		response.addCookie(accessCookie);
		response.addCookie(refreshCookie);
	}
}
