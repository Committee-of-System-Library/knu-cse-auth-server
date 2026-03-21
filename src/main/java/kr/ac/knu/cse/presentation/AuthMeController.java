package kr.ac.knu.cse.presentation;

import java.util.Map;
import java.util.Optional;
import kr.ac.knu.cse.domain.provider.Provider;
import kr.ac.knu.cse.domain.provider.ProviderRepository;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthMeController {

    private static final String PROVIDER_NAME = "KEYCLOAK";

    private final ProviderRepository providerRepository;
    private final StudentRepository studentRepository;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        String providerKey = oidcUser.getSubject();
        String email = oidcUser.getEmail();

        Optional<Provider> providerOpt = providerRepository.findByProviderNameAndProviderKey(
                PROVIDER_NAME, providerKey
        );

        if (providerOpt.isEmpty() || providerOpt.get().getStudentId() == null) {
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "email", email != null ? email : "",
                    "needsConsent", true
            ));
        }

        Optional<Student> studentOpt = studentRepository.findById(providerOpt.get().getStudentId());

        if (studentOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "email", email != null ? email : ""
            ));
        }

        Student student = studentOpt.get();
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "email", email != null ? email : "",
                "role", student.getRole() != null ? student.getRole().name() : "USER"
        ));
    }
}
