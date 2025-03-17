package kr.ac.knu.cse.auth.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.knu.cse.auth.presentation.dto.JwtTokens;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.token.application.JwtTokenService;
import kr.ac.knu.cse.token.application.RefreshTokenService;
import kr.ac.knu.cse.token.domain.Token;
import kr.ac.knu.cse.token.domain.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RefreshController {

	private final JwtTokenService jwtTokenService;
	private final RefreshTokenService refreshTokenService;

	@PostMapping("/refresh")
	public ResponseEntity<ApiSuccessResult<JwtTokens>> refreshToken(
		HttpServletRequest request,
		Authentication authentication
	) {
		// Refresh Token 유효성 검사
		String refreshToken = jwtTokenService.resolveToken(request);
		String email = jwtTokenService.extractEmail(jwtTokenService.extractClaims(refreshToken));
		refreshTokenService.validateRefreshToken(email, refreshToken);

		// 새로운 토큰 발급
		Token newAccessToken = jwtTokenService.generateToken(authentication, TokenType.ACCESS_TOKEN);
		Token newRefreshToken = jwtTokenService.generateToken(authentication, TokenType.REFRESH_TOKEN);

		refreshTokenService.updateRefreshToken(email, newRefreshToken.value());

		JwtTokens tokens = JwtTokens.builder()
			.accessToken(newAccessToken)
			.refreshToken(newRefreshToken)
			.build();

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, tokens));
	}
}
