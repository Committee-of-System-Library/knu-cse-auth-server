package kr.ac.knu.cse.dues.presentation;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.ac.knu.cse.dues.application.DuesCommandService;
import kr.ac.knu.cse.dues.domain.Dues;
import kr.ac.knu.cse.dues.presentation.dto.DuesReadDto;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.security.annotation.LoggedInProvider;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dues")
public class DuesCommandController {

	private final DuesCommandService duesCommandService;

	@PostMapping
	public void submitDues(@RequestParam("file") final MultipartFile file) {
		try (final InputStream in = file.getInputStream()) {
			duesCommandService.submitAll(in);
		} catch (final IOException exception) {
			throw new IllegalArgumentException("파일을 읽는 중 오류가 발생했습니다.");
		}
	}

	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiSuccessResult<DuesReadDto>> getMyDues(
		@LoggedInProvider PrincipalDetails principalDetails
	) {
		Dues dues = duesCommandService.getMyDues(principalDetails.student());
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, DuesReadDto.from(principalDetails.student(), dues)));
	}
}
