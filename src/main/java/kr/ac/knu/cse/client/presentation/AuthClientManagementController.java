package kr.ac.knu.cse.client.presentation;

import jakarta.validation.Valid;
import kr.ac.knu.cse.client.application.AuthClientService;
import kr.ac.knu.cse.client.presentation.dto.AuthClientDetailResponse;
import kr.ac.knu.cse.client.presentation.dto.AuthClientResponse;
import kr.ac.knu.cse.client.presentation.dto.CreateClientRequest;
import kr.ac.knu.cse.client.presentation.dto.UpdateClientRequest;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/services")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AuthClientManagementController {

    private final AuthClientService authClientService;

    @PostMapping
    public ResponseEntity<ApiSuccessResult<Long>> createService(
            @Valid @RequestBody CreateClientRequest request
    ) {
        log.info("클라이언트 생성 요청 - ServiceName: {}", request.getClientName());

        Long clientId = authClientService.createAuthClient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, clientId));
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResult<List<AuthClientResponse>>> getAllServices() {
        List<AuthClientResponse> services = authClientService.getAllAuthClients();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, services));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiSuccessResult<List<AuthClientResponse>>> getActiveServices() {
        List<AuthClientResponse> activeServices = authClientService.getActiveAuthClients();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, activeServices));
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ApiSuccessResult<AuthClientDetailResponse>> getServiceDetail(
            @PathVariable Long clientId
    ) {
        AuthClientDetailResponse serviceDetail = authClientService.getAuthClientDetail(clientId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, serviceDetail));
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ApiSuccessResult<Void>> updateService(
            @PathVariable Long clientId,
            @Valid @RequestBody UpdateClientRequest request
    ) {
        log.info("클라이언트 정보 수정 요청 - ClientId: {}, ServiceName: {}",
                clientId, request.getClientName());

        authClientService.updateAuthClient(clientId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK));
    }

    @PostMapping("/{clientId}/regenerate-secret")
    public ResponseEntity<ApiSuccessResult<Void>> regenerateSecret(
            @PathVariable Long clientId
    ) {
        log.info("JWT Secret 재생성 요청 - ClientId: {}", clientId);

        authClientService.regenerateSecret(clientId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK));
    }

    @PostMapping("/{clientId}/activate")
    public ResponseEntity<ApiSuccessResult<Void>> activateService(
            @PathVariable Long clientId
    ) {
        log.info("클라이언트 활성화 요청 - ClientId: {}", clientId);

        authClientService.activateAuthClient(clientId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK));
    }

    @PostMapping("/{clientId}/deactivate")
    public ResponseEntity<ApiSuccessResult<Void>> deactivateService(
            @PathVariable Long clientId
    ) {
        log.info("클라이언트 비활성화 요청 - ClientId: {}", clientId);

        authClientService.deactivateAuthClient(clientId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK));
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<ApiSuccessResult<Void>> deleteService(
            @PathVariable Long clientId
    ) {
        log.info("클라이언트 삭제 요청 - ClientId: {}", clientId);

        authClientService.deleteAuthClient(clientId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK));
    }
}
