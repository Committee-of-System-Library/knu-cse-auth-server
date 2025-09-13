package kr.ac.knu.cse.auth.presentation;

import kr.ac.knu.cse.auth.presentation.dto.TokenInfoDto;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.security.annotation.LoggedInProvider;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.student.domain.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthorizationController {

	@GetMapping("/token-info")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiSuccessResult<TokenInfoDto>> tokenInfo(
		@LoggedInProvider PrincipalDetails principalDetails
	) {
		log.info("토큰 정보 요청 - 사용자: {}", principalDetails.getName());
		Provider provider = principalDetails.provider();
		Student student = principalDetails.student();

		log.debug("Provider: {}, Student: {}", provider, student);

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, TokenInfoDto.from(student, provider)));
	}
}
