package kr.ac.knu.cse.auth.presentation;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.token.application.JwtTokenService;
import kr.ac.knu.cse.token.application.RefreshTokenService;
import kr.ac.knu.cse.token.presentation.TokenProvisioner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RefreshController {

	private final JwtTokenService jwtTokenService;
	private final RefreshTokenService refreshTokenService;
	private final TokenProvisioner tokenProvisioner;

	@PostMapping("/refresh")
	public ResponseEntity<ApiSuccessResult<?>> refreshToken(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {
		String refreshToken = jwtTokenService.resolveToken(request, "refresh_token");
		String email = jwtTokenService.extractEmail(jwtTokenService.extractClaims(refreshToken));
		refreshTokenService.validateRefreshToken(email, refreshToken);

		// 토큰 생섬 및 쿠기 주입
		tokenProvisioner.tokenIssue(authentication, response);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, "Token Issue Successed."));
	}
}
