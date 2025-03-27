package kr.ac.knu.cse.provider.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.provider.presentation.dto.ProviderResponse;
import kr.ac.knu.cse.provider.presentation.dto.ProviderSearchFilter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manage/providers")
public class ProviderQueryController {

	private final ProviderRepository providerRepository;

	@GetMapping
	@PreAuthorize("hasRole('FINANCE')")
	public ResponseEntity<ApiSuccessResult<Page<ProviderResponse>>> getProviders(
		@ModelAttribute final ProviderSearchFilter filter,
		final Pageable pageable
	) {
		Page<ProviderResponse> page = providerRepository.findProviders(filter, pageable);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, page));
	}
}
