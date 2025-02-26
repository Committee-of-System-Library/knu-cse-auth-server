package kr.ac.knu.cse.auth.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.token.application.JwtTokenService;
import kr.ac.knu.cse.token.application.RefreshTokenService;
import kr.ac.knu.cse.token.domain.JwtTokens;
import kr.ac.knu.cse.token.domain.RefreshToken;
import kr.ac.knu.cse.token.domain.Token;
import kr.ac.knu.cse.token.domain.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class RefreshController {

	private final JwtTokenService jwtTokenService;
	private final RefreshTokenService refreshTokenService;

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(Authentication authentication) {
		if (authentication == null || !(authentication.getPrincipal() instanceof PrincipalDetails principalDetails)) {
			return ResponseEntity.badRequest().body("Invalid refresh token authentication.");
		}

		String email = principalDetails.getName();

		String storedRefreshValue = refreshTokenService.getRefreshToken(email);
		if (storedRefreshValue == null) {
			return ResponseEntity.badRequest().body("No stored refresh token. Please login again.");
		}

		// 새 Access 토큰, 새 Refresh 토큰 생성
		Token newAccessToken = jwtTokenService.generateToken(authentication, TokenType.ACCESS_TOKEN);
		Token newRefreshToken = jwtTokenService.generateToken(authentication, TokenType.REFRESH_TOKEN);

		// 새 Refresh 토큰을 DB(혹은 Redis)에 업데이트
		refreshTokenService.updateRefreshToken(
			RefreshToken.builder()
				.email(email)
				.refreshToken(newRefreshToken.value())
				.build()
		);

		// 클라이언트에 반환
		JwtTokens tokens = JwtTokens.builder()
			.accessToken(newAccessToken)
			.refreshToken(newRefreshToken)
			.build();

		return ResponseEntity.ok(tokens);
	}
}
