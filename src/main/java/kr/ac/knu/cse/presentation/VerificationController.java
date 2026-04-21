package kr.ac.knu.cse.presentation;

import jakarta.validation.Valid;
import java.util.List;
import kr.ac.knu.cse.application.VerificationService;
import kr.ac.knu.cse.domain.verification.VerificationRequest;
import kr.ac.knu.cse.global.exception.auth.InvalidOidcUserException;
import lombok.RequiredArgsConstructor;
import kr.ac.knu.cse.presentation.dto.VerificationSubmitRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/verification")
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping
    public ResponseEntity<VerificationRequest> submit(
            @AuthenticationPrincipal OidcUser oidcUser,
            @Valid @RequestBody VerificationSubmitRequest request
    ) {
        if (oidcUser == null) {
            throw new InvalidOidcUserException();
        }

        Long studentId = extractStudentId(oidcUser);

        VerificationRequest result = verificationService.submitRequest(
                studentId,
                request.studentNumber(),
                request.evidence()
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<List<VerificationRequest>> getMyRequests(
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        if (oidcUser == null) {
            throw new InvalidOidcUserException();
        }

        Long studentId = extractStudentId(oidcUser);
        List<VerificationRequest> requests = verificationService.getMyRequests(studentId);

        return ResponseEntity.ok(requests);
    }

    private Long extractStudentId(OidcUser oidcUser) {
        Object studentIdClaim = oidcUser.getClaim("student_id");
        if (studentIdClaim == null) {
            throw new InvalidOidcUserException();
        }
        return Long.valueOf(studentIdClaim.toString());
    }
}
