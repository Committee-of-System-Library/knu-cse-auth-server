package kr.ac.knu.cse.infrastructure.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakAdminProperties(
        String baseUrl,
        String realm,
        Admin admin
) {
    public record Admin(String clientId, String clientSecret) {}
}
