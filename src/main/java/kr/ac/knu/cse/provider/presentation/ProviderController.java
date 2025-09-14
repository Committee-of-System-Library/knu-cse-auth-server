package kr.ac.knu.cse.provider.presentation;

import jakarta.validation.Valid;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.provider.application.ProviderService;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.provider.presentation.dto.PatchUpdateProviderReq;
import kr.ac.knu.cse.provider.presentation.dto.PostCreateProviderReq;
import kr.ac.knu.cse.provider.presentation.dto.ProviderResponse;
import kr.ac.knu.cse.provider.presentation.dto.ProviderSearchFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/providers")
@PreAuthorize("hasRole('FINANCE')")
@Slf4j
public class ProviderController {

    private final ProviderRepository providerRepository;
    private final ProviderService providerService;

    @GetMapping
    public ResponseEntity<ApiSuccessResult<Page<ProviderResponse>>> getProviders(
            @ModelAttribute final ProviderSearchFilter filter,
            final Pageable pageable
    ) {
        log.info("Provider 목록 조회 요청 - 필터: {}, 페이지: {}", filter, pageable);
        Page<ProviderResponse> page = providerRepository.findProviders(filter, pageable);
        log.info("Provider 목록 조회 완료 - 총 {}개", page.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, page));
    }

    @PostMapping
    public ResponseEntity<ApiSuccessResult<?>> createProvider(
            @Valid @RequestBody PostCreateProviderReq req
    ) {
        log.info("Provider 생성 요청 - {}", req);
        Long id = providerService.createProvider(req);
        log.info("Provider 생성 완료 - ID: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Provider created with ID: " + id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiSuccessResult<?>> updateProvider(
            @PathVariable Long id,
            @Valid @RequestBody PatchUpdateProviderReq req
    ) {
        log.info("Provider 수정 요청 - ID: {}, {}", id, req);
        providerService.updateProvider(id, req);
        log.info("Provider 수정 완료 - ID: {}", id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Provider updated"));
    }

    @DeleteMapping
    public ResponseEntity<ApiSuccessResult<?>> deleteProviders(
            @RequestParam("ids") List<Long> ids
    ) {
        log.info("Provider 삭제 요청 - ID 목록: {}", ids);
        providerService.deleteProviders(ids);
        log.info("Provider 삭제 완료");
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Provider deleted."));
    }
}
