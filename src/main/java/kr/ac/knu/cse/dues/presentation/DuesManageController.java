package kr.ac.knu.cse.dues.presentation;

import jakarta.validation.Valid;
import kr.ac.knu.cse.dues.application.DuesManageService;
import kr.ac.knu.cse.dues.application.mapper.DuesMapper;
import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.dues.presentation.dto.CreateDuesPostReq;
import kr.ac.knu.cse.dues.presentation.dto.DuesListResponse;
import kr.ac.knu.cse.dues.presentation.dto.DuesSearchFilter;
import kr.ac.knu.cse.dues.presentation.dto.UpdateDuesPatchReq;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dues")
@PreAuthorize("hasRole('FINANCE')")
public class DuesManageController {

	private final DuesRepository duesRepository;
	private final DuesManageService duesManageService;

	@GetMapping
	public ResponseEntity<ApiSuccessResult<Page<DuesListResponse>>> getDuesList(
		@ModelAttribute DuesSearchFilter filter,
		Pageable pageable
	) {
		Page<DuesListResponse> page = duesRepository.findDuesList(filter, pageable);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, page));
	}

	@PostMapping
	public ResponseEntity<ApiSuccessResult<?>> createDues(
		@Valid @RequestBody CreateDuesPostReq requestBody
	) {
		Long id = duesManageService.createDues(DuesMapper.toCreateDuesDto(requestBody));
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(HttpStatus.CREATED, "Dues created with ID: " + id));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiSuccessResult<?>> updateDues(
		@PathVariable Long id,
		@Valid @RequestBody UpdateDuesPatchReq requestBody
	) {
		duesManageService.updateDues(id, DuesMapper.toUpdateDuesDto(requestBody));
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, "Dues updated"));
	}

	@DeleteMapping
	public ResponseEntity<ApiSuccessResult<?>> deleteDues(@RequestParam("ids") List<Long> ids) {
		duesManageService.deleteDues(ids);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, "선택된 납부 내역이 삭제되었습니다."));
	}
}
