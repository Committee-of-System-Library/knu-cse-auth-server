package kr.ac.knu.cse.dues.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.dues.presentation.dto.DuesListResponse;
import kr.ac.knu.cse.dues.presentation.dto.DuesSearchFilter;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manage/dues")
public class DuesQueryController {

	private final DuesRepository duesRepository;

	@GetMapping
	@PreAuthorize("hasRole('FINANCE')")
	public ResponseEntity<ApiSuccessResult<Page<DuesListResponse>>> getDuesList(
		@ModelAttribute DuesSearchFilter filter,
		Pageable pageable
	) {
		Page<DuesListResponse> page = duesRepository.findDuesList(filter, pageable);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, page));
	}
}
