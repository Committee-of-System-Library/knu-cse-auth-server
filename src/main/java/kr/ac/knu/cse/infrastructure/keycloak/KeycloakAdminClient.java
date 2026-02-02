package kr.ac.knu.cse.infrastructure.keycloak;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KeycloakAdminClient {

    private final WebClient keycloakWebClient;
    private final KeycloakAdminProperties keycloakAdminProperties;

    public void upsertStudentIdAttribute(String keycloakUserId, Long studentId) {
        String token = getAdminAccessToken();
        Map<String, Object> payload = createAttributes(studentId);
        requestUpdateOfToken(keycloakUserId, token, payload);
    }

    private String getAdminAccessToken() {
        Map<String, Object> response = requestToken();
        validateTokenResponse(response);
        return String.valueOf(response.get("access_token"));
    }

    private Map<String, Object> requestToken() {
        return keycloakWebClient.post()
                .uri(keycloakAdminProperties.baseUrl() + "/realms/" + keycloakAdminProperties.realm() + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", keycloakAdminProperties.admin().clientId())
                        .with("client_secret", keycloakAdminProperties.admin().clientSecret()))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    private void validateTokenResponse(Map<String, Object> tokenResponse) {
        if (tokenResponse == null || tokenResponse.get("access_token") == null) {
            throw new IllegalStateException("Failed to get Keycloak admin access token");
        }
    }

    private Map<String, Object> createAttributes(Long studentId) {
        return Map.of(
                "attributes", Map.of(
                        "studentId", List.of(String.valueOf(studentId))
                )
        );
    }

    private void requestUpdateOfToken(
            String keycloakUserId,
            String token,
            Map<String, Object> payload) {

        keycloakWebClient.put()
                .uri(keycloakAdminProperties.baseUrl() + "/admin/realms/" + keycloakAdminProperties.realm() + "/users/" + keycloakUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(token))
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void ensureRealmRole(String keycloakUserId, String roleName) {
        String token = getAdminAccessToken();

        Map<String, Object> role = requestOriginalRole(roleName, token);
        validateRole(roleName, role);

        requestUpdateOfRole(keycloakUserId, token, role);
    }

    private Map<String, Object> requestOriginalRole(String roleName, String token) {
        return keycloakWebClient.get()
                .uri(keycloakAdminProperties.baseUrl() + "/admin/realms/" + keycloakAdminProperties.realm() + "/roles/" + roleName)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    private void validateRole(String roleName, Map<String, Object> role) {
        if (role == null) {
            throw new IllegalStateException("Keycloak role not found: " + roleName);
        }
    }

    private void requestUpdateOfRole(String keycloakUserId, String token, Map<String, Object> role) {
        keycloakWebClient.post()
                .uri(keycloakAdminProperties.baseUrl() + "/admin/realms/" + keycloakAdminProperties.realm()
                        + "/users/" + keycloakUserId + "/role-mappings/realm")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(token))
                .bodyValue(List.of(role))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}

