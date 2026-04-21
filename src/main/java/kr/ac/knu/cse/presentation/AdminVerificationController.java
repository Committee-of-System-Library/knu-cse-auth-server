package kr.ac.knu.cse.presentation;

import java.util.List;
import kr.ac.knu.cse.application.AdminAuthService;
import kr.ac.knu.cse.application.AdminVerificationService;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.verification.VerificationRequest;
import kr.ac.knu.cse.domain.verification.VerificationStatus;
import kr.ac.knu.cse.presentation.dto.VerificationReviewRequest;
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
@RequestMapping("/admin/verifications")
public class AdminVerificationController {

    private final AdminAuthService adminAuthService;
    private final AdminVerificationService adminVerificationService;

    @GetMapping
    public ResponseEntity<List<VerificationRequest>> findAll(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestParam(value = "status", required = false) VerificationStatus status
    ) {
        adminAuthService.requireAdmin(oidcUser);

        List<VerificationRequest> requests = (status != null)
                ? adminVerificationService.findByStatus(status)
                : adminVerificationService.findAll();

        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VerificationRequest> findById(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(adminVerificationService.findById(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<VerificationRequest> approve(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id,
            @RequestBody(required = false) VerificationReviewRequest request
    ) {
        Student admin = adminAuthService.requireAdmin(oidcUser);
        String comment = (request != null) ? request.comment() : null;

        return ResponseEntity.ok(
                adminVerificationService.approve(id, admin.getId(), comment)
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<VerificationRequest> reject(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id,
            @RequestBody(required = false) VerificationReviewRequest request
    ) {
        Student admin = adminAuthService.requireAdmin(oidcUser);
        String comment = (request != null) ? request.comment() : null;

        return ResponseEntity.ok(
                adminVerificationService.reject(id, admin.getId(), comment)
        );
    }
}
