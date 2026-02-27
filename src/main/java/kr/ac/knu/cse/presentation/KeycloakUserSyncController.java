package kr.ac.knu.cse.presentation;

import jakarta.validation.Valid;
import kr.ac.knu.cse.infrastructure.keycloak.KeycloakAdminClient;
import kr.ac.knu.cse.presentation.dto.DeactivateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/keycloak/users")
public class KeycloakUserSyncController {

    private final KeycloakAdminClient keycloakAdminClient;

    @PostMapping("/deactivate")
    public ResponseEntity<Void> deactivate(
            @Valid @RequestBody DeactivateUserRequest request
    ) {
        keycloakAdminClient.disableUser(request.subject());
        return ResponseEntity.noContent().build();
    }
}
