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

	/**
	 * Authentication 객체와 이메일을 기반으로 액세스/리프레시 토큰을 생성하고,
	 * 세션에 저장된 redirectUrl (없으면 기본값 "/")에 토큰을 쿼리 파라미터로 붙인 최종 URL을 반환한다.
	 */
	public String generateRedirectUrl(HttpServletRequest request, Authentication authentication, String email) {
		Token accessToken = jwtTokenService.generateToken(authentication, TokenType.ACCESS_TOKEN);
		Token refreshToken = jwtTokenService.generateToken(authentication, TokenType.REFRESH_TOKEN);
		refreshTokenService.updateRefreshToken(email, refreshToken.value());

		String redirectUrl = (String) request.getSession().getAttribute("redirectUrl");
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
