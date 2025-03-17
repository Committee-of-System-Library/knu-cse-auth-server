package kr.ac.knu.cse.auth.applicaiton;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.knu.cse.token.application.JwtTokenService;
import kr.ac.knu.cse.token.application.RefreshTokenService;
import kr.ac.knu.cse.token.domain.Token;
import kr.ac.knu.cse.token.domain.TokenType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenRedirectProvider {
	private final JwtTokenService jwtTokenService;
	private final RefreshTokenService refreshTokenService;

	public String generateRedirectUrl(HttpServletRequest request, Authentication authentication, String email) {
		Token accessToken = jwtTokenService.generateToken(authentication, TokenType.ACCESS_TOKEN);
		Token refreshToken = jwtTokenService.generateToken(authentication, TokenType.REFRESH_TOKEN);
		refreshTokenService.updateRefreshToken(email, refreshToken.value());

		String redirectUrl = (String)request.getSession().getAttribute("redirectUrl");
		if (redirectUrl == null || redirectUrl.isBlank()) {
			redirectUrl = "/";
		}

		return UriComponentsBuilder.fromUriString(redirectUrl)
			.queryParam("access_token", accessToken.value())
			.queryParam("refresh_token", refreshToken.value())
			.build()
			.toUriString();
	}
}
