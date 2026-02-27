package kr.ac.knu.cse.infrastructure.keycloak.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakAdminProperties(
        @NotBlank String baseUrl,
        @NotBlank String realm,
        @NotNull @Valid Admin admin
) {
    public record Admin(@NotBlank String clientId, @NotBlank String clientSecret) {}
}
