package kr.ac.knu.cse.infrastructure.keycloak;

import java.util.List;
import java.util.Map;
import kr.ac.knu.cse.infrastructure.keycloak.config.KeycloakAdminProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KeycloakAdminClient {

    private final RestClient keycloakRestClient;
    private final KeycloakAdminProperties keycloakAdminProperties;
    private final KeycloakAdminTokenClient tokenClient;
    private final KeycloakExceptionTranslator exceptionTranslator;

    public void updateRoleInKeycloak(String keycloakUserId, String roleName) {
        String token = tokenClient.getAdminAccessToken();

        try {
            Map<String, Object> role = requestOriginalRole(roleName, token);
            validateRole(roleName, role);

            requestUpdateOfRole(keycloakUserId, token, role);
        } catch (Exception e) {
            throw exceptionTranslator.translate(e);
        }

    }

    private Map<String, Object> requestOriginalRole(String roleName, String token) {
        return keycloakRestClient.get()
                .uri(keycloakAdminProperties.baseUrl()
                        + "/admin/realms/" + keycloakAdminProperties.realm()
                        + "/roles/" + roleName)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .body(Map.class);
    }

    private void validateRole(String roleName, Map<String, Object> role) {
        if (role == null) {
            throw new IllegalStateException("Keycloak role not found: " + roleName);
        }
    }

    private void requestUpdateOfRole(String keycloakUserId, String token, Map<String, Object> role) {
        keycloakRestClient.post()
                .uri(keycloakAdminProperties.baseUrl()
                        + "/admin/realms/" + keycloakAdminProperties.realm()
                        + "/users/" + keycloakUserId + "/role-mappings/realm")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(token))
                .body(List.of(role))
                .retrieve()
                .toBodilessEntity();
    }

    public void disableUser(String subject) {
        String adminAccessToken = tokenClient.getAdminAccessToken();

        try {
            keycloakRestClient.put()
                    .uri(keycloakAdminProperties.baseUrl()
                                    + "/admin/realms/{realm}/users/{id}",
                            keycloakAdminProperties.realm(), subject)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("enabled", false))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw exceptionTranslator.translate(e);
        }
    }
}
