package kr.ac.knu.cse.presentation;

import java.util.List;
import kr.ac.knu.cse.application.AdminAppService;
import kr.ac.knu.cse.application.AdminAuthService;
import kr.ac.knu.cse.domain.application.ClientApplication;
import kr.ac.knu.cse.domain.application.ClientApplicationStatus;
import kr.ac.knu.cse.presentation.dto.AppApproveResponse;
import kr.ac.knu.cse.presentation.dto.AppRejectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appfn/api/admin/apps")
public class AdminAppController {

    private final AdminAuthService adminAuthService;
    private final AdminAppService adminAppService;

    @GetMapping
    public ResponseEntity<List<ClientApplication>> findAll(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestParam(value = "status", required = false) ClientApplicationStatus status
    ) {
        adminAuthService.requireAdmin(oidcUser);

        List<ClientApplication> apps = (status != null)
                ? adminAppService.findByStatus(status)
                : adminAppService.findAll();

        return ResponseEntity.ok(apps);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientApplication> findById(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(adminAppService.findById(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<AppApproveResponse> approve(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(adminAppService.approve(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ClientApplication> reject(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id,
            @RequestBody(required = false) AppRejectRequest request
    ) {
        adminAuthService.requireAdmin(oidcUser);
        String reason = (request != null) ? request.reason() : null;
        return ResponseEntity.ok(adminAppService.reject(id, reason));
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<ClientApplication> suspend(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(adminAppService.suspend(id));
    }
}
