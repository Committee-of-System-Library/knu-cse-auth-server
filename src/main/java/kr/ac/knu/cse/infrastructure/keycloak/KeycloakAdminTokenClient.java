package kr.ac.knu.cse.infrastructure.keycloak;

import java.util.Map;
import kr.ac.knu.cse.infrastructure.keycloak.config.KeycloakAdminProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KeycloakAdminTokenClient {


    private final RestClient keycloakRestClient;
    private final KeycloakAdminProperties keycloakAdminProperties;
    private final KeycloakExceptionTranslator exceptionTranslator;

    public String getAdminAccessToken() {
        try {
            Map<String, Object> response = requestToken();
            return String.valueOf(response.get("access_token"));
        } catch (Exception e) {
            throw exceptionTranslator.translate(e);
        }
    }

    private Map<String, Object> requestToken() {
        return keycloakRestClient.post()
                .uri(keycloakAdminProperties.baseUrl()
                        + "/realms/" + keycloakAdminProperties.realm()
                        + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(
                        new LinkedMultiValueMap<>() {{
                            add("grant_type", "client_credentials");
                            add("client_id", keycloakAdminProperties.admin().clientId());
                            add("client_secret", keycloakAdminProperties.admin().clientSecret());
                        }}
                )
                .retrieve()
                .body(Map.class);
    }
}
