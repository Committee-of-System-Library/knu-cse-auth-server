package kr.ac.knu.cse.presentation;

import jakarta.validation.Valid;
import kr.ac.knu.cse.application.SignupService;
import kr.ac.knu.cse.application.dto.SignupCommand;
import kr.ac.knu.cse.application.dto.SignupResponse;
import kr.ac.knu.cse.global.exception.auth.InvalidOidcUserException;
import kr.ac.knu.cse.global.exception.auth.MissingEmailClaimException;
import kr.ac.knu.cse.presentation.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignupController {

    private static final String PROVIDER_NAME = "KEYCLOAK";


    private final SignupService signupService;

    @PostMapping
    public ResponseEntity<SignupResponse> signup(
            @AuthenticationPrincipal OidcUser oidcUser,
            @Valid @RequestBody SignupRequest request
    ) {
        if (oidcUser == null) {
            throw new InvalidOidcUserException();
        }

        String email = extractEmail(oidcUser);
        String subject = extractSubject(oidcUser);
        String fullName = extractFullName(oidcUser);

        SignupCommand command = new SignupCommand(
                PROVIDER_NAME,
                subject,
                email,
                request.studentNumber(),
                fullName,
                request.major(),
                request.grade()
        );

        SignupResponse response = signupService.signup(command);

        return ResponseEntity.ok(response);
    }

    private String extractEmail(OidcUser oidcUser) {
        String email = oidcUser.getEmail();

        if (email == null || email.isBlank()) {
            throw new MissingEmailClaimException();
        }
        return email;
    }

    private String extractSubject(OidcUser oidcUser) {
        String subject = oidcUser.getSubject();

        if (subject == null || subject.isBlank()) {
            throw new InvalidOidcUserException();
        }

        return subject;
    }

    private String extractFullName(OidcUser oidcUser) {
        String fullName = oidcUser.getFullName();

        if (fullName == null || fullName.isBlank()) {
            throw new InvalidOidcUserException();
        }

        return fullName;
    }
}
