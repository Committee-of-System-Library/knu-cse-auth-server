package kr.ac.knu.cse.auth.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.knu.cse.security.details.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthorizationController {

	@GetMapping("/tokeninfo")
	public ResponseEntity<?> tokenInfo(Authentication authentication) {
		if (authentication == null || !(authentication.getPrincipal() instanceof PrincipalDetails principalDetails)) {
			return ResponseEntity.badRequest().body("Invalid authentication.");
		}

		String email = principalDetails.getName();

		// 예시로 email, roles 등 사용자 정보 반환
		return ResponseEntity.ok().body(
			String.format("인증된 사용자: %s (role: %s)",
				email,
				principalDetails.getAuthorities())
		);
	}
}
