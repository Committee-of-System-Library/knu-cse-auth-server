package kr.ac.knu.cse.presentation;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import kr.ac.knu.cse.application.AdminAuthService;
import kr.ac.knu.cse.application.RegistryService;
import kr.ac.knu.cse.application.dto.RegistryUploadResult;
import kr.ac.knu.cse.domain.registry.CseStudentRegistry;
import kr.ac.knu.cse.presentation.dto.RegistryAddRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appfn/api/admin/registry")
public class RegistryAdminController {

    private final AdminAuthService adminAuthService;
    private final RegistryService registryService;

    @GetMapping
    public ResponseEntity<List<CseStudentRegistry>> findAll(
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(registryService.findAll());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RegistryUploadResult> uploadCsv(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        adminAuthService.requireAdmin(oidcUser);
        RegistryUploadResult result = registryService.uploadCsv(file.getInputStream());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<CseStudentRegistry> addManually(
            @AuthenticationPrincipal OidcUser oidcUser,
            @Valid @RequestBody RegistryAddRequest request
    ) {
        adminAuthService.requireAdmin(oidcUser);
        CseStudentRegistry registry = registryService.addManually(
                request.studentNumber(),
                request.name(),
                request.major(),
                request.grade()
        );
        return ResponseEntity.ok(registry);
    }

    @DeleteMapping("/{studentNumber}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable String studentNumber
    ) {
        adminAuthService.requireAdmin(oidcUser);
        registryService.delete(studentNumber);
        return ResponseEntity.noContent().build();
    }
}
