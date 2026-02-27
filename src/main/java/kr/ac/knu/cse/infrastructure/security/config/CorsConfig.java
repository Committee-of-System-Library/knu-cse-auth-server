package kr.ac.knu.cse.infrastructure.security.config;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import kr.ac.knu.cse.domain.redirect.RedirectUriProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final RedirectUriProperties redirectUriProperties;

    @Value("${app.frontend.base-url}")
    private String baseUrl;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        Set<String> allowedOrigins = redirectUriProperties.redirectAllowlist().stream()
                .map(this::normalizeOrigin)
                .collect(Collectors.toSet());
        allowedOrigins.add(normalizeOrigin(baseUrl));

        config.setAllowedOrigins(allowedOrigins.stream().toList());
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    private String normalizeOrigin(String origin) {
        if (origin.endsWith("/")) {
            return origin.substring(0, origin.length() - 1);
        }
        return origin;
    }
}
