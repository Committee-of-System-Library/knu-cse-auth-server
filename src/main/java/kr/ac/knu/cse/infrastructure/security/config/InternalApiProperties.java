package kr.ac.knu.cse.infrastructure.security.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.internal")
public record InternalApiProperties(
        @NotBlank String pathPrefix,
        @NotBlank String headerName,
        @NotBlank String apiKey
) {}
