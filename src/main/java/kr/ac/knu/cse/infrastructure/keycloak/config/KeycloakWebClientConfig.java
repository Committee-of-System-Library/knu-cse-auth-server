package kr.ac.knu.cse.infrastructure.keycloak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

//HTTP Client for keycloak REST Admin APIs
@Configuration
public class KeycloakWebClientConfig {

    @Bean
    public WebClient keycloakWebClient() {
        return WebClient.builder().build();
    }
}
