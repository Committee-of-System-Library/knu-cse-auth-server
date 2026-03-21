package kr.ac.knu.cse.presentation;

import jakarta.validation.Valid;
import java.util.List;
import kr.ac.knu.cse.application.DeveloperAppService;
import kr.ac.knu.cse.application.DeveloperAuthService;
import kr.ac.knu.cse.domain.application.ClientApplication;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.presentation.dto.AppRegisterRequest;
import kr.ac.knu.cse.presentation.dto.AppUpdateRequest;
import kr.ac.knu.cse.presentation.dto.SecretRegenerateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appfn/api/developer/apps")
public class DeveloperAppController {

    private final DeveloperAuthService developerAuthService;
    private final DeveloperAppService developerAppService;

    @PostMapping
    public ResponseEntity<ClientApplication> register(
            @AuthenticationPrincipal OidcUser oidcUser,
            @Valid @RequestBody AppRegisterRequest request
    ) {
        Student student = developerAuthService.requireCseStudent(oidcUser);

        ClientApplication app = developerAppService.register(
                student.getId(),
                request.appName(),
                request.description(),
                request.redirectUris(),
                request.homepageUrl()
        );

        return ResponseEntity.ok(app);
    }

    @GetMapping
    public ResponseEntity<List<ClientApplication>> findMyApps(
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        Student student = developerAuthService.requireCseStudent(oidcUser);
        return ResponseEntity.ok(developerAppService.findMyApps(student.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientApplication> findById(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id
    ) {
        Student student = developerAuthService.requireCseStudent(oidcUser);
        return ResponseEntity.ok(developerAppService.findById(id, student.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientApplication> update(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id,
            @Valid @RequestBody AppUpdateRequest request
    ) {
        Student student = developerAuthService.requireCseStudent(oidcUser);

        ClientApplication app = developerAppService.update(
                id,
                student.getId(),
                request.appName(),
                request.description(),
                request.redirectUris(),
                request.homepageUrl()
        );

        return ResponseEntity.ok(app);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id
    ) {
        Student student = developerAuthService.requireCseStudent(oidcUser);
        developerAppService.delete(id, student.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/regenerate-secret")
    public ResponseEntity<SecretRegenerateResponse> regenerateSecret(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id
    ) {
        Student student = developerAuthService.requireCseStudent(oidcUser);
        String newSecret = developerAppService.regenerateSecret(id, student.getId());

        ClientApplication app = developerAppService.findById(id, student.getId());

        return ResponseEntity.ok(new SecretRegenerateResponse(app.getClientId(), newSecret));
    }
}
