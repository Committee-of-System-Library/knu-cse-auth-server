package kr.ac.knu.cse.infrastructure.keycloak.config;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

//HTTP Client for keycloak REST Admin APIs
@Configuration
public class KeycloakRestClientConfig {

    @Value("${keycloak.http.connect-timeout-seconds:5}")
    private int connectTimeout;
    @Value("${keycloak.http.read-timeout-seconds:10}")
    private int readTimeout;

    @Bean
    public RestClient keycloakRestClient() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(connectTimeout))
                        .build()
        );

        factory.setReadTimeout(Duration.ofSeconds(readTimeout));

        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}
