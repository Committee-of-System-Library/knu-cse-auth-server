package kr.ac.knu.cse.dues.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.ac.knu.cse.dues.application.DuesManageService;
import kr.ac.knu.cse.dues.application.mapper.DuesMapper;
import kr.ac.knu.cse.dues.presentation.dto.CreateDuesPostReq;
import kr.ac.knu.cse.dues.presentation.dto.UpdateDuesPatchReq;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dues/manage")
@PreAuthorize("hasRole('FINANCE')")
public class DuesManageController {

	private final DuesManageService duesManageService;

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

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiSuccessResult<?>> deleteDues(@PathVariable Long id) {
		duesManageService.deleteDues(id);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, "Dues deleted"));
	}
}
