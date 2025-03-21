package kr.ac.knu.cse.provider.presentation;

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
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.provider.application.ProviderManageService;
import kr.ac.knu.cse.provider.presentation.dto.PatchUpdateProviderReq;
import kr.ac.knu.cse.provider.presentation.dto.PostCreateProviderReq;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manage/providers")
@PreAuthorize("hasRole('FINANCE')")
public class ProviderManageController {

	private final ProviderManageService providerManageService;

	@PostMapping
	public ResponseEntity<ApiSuccessResult<?>> createProvider(
		@Valid @RequestBody PostCreateProviderReq req
	) {
		Long id = providerManageService.createProvider(req);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(HttpStatus.CREATED, "Provider created with ID: " + id));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiSuccessResult<?>> updateProvider(
		@PathVariable Long id,
		@Valid @RequestBody PatchUpdateProviderReq req
	) {
		providerManageService.updateProvider(id, req);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Provider updated"));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiSuccessResult<?>> deleteProvider(
		@PathVariable Long id
	) {
		providerManageService.deleteProvider(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Provider deleted"));
	}
}

