package kr.ac.knu.cse.infrastructure.security.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import kr.ac.knu.cse.infrastructure.security.filter.InternalApiKeyFilter;
import kr.ac.knu.cse.infrastructure.security.filter.OAuth2LoginFailureHandler;
import kr.ac.knu.cse.infrastructure.security.filter.OAuth2LoginSuccessHandler;
import kr.ac.knu.cse.infrastructure.security.filter.OAuth2LogoutSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler loginSuccessHandler;
    private final OAuth2LoginFailureHandler loginFailureHandler;
    private final OAuth2LogoutSuccessHandler logoutSuccessHandler;
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final InternalApiKeyFilter internalApiKeyFilter;
    private final InternalApiProperties internalApiProperties;
    private final Environment environment;

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(authorizationRequestResolver)
                        )
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(internalPathPattern()).permitAll()
                        .requestMatchers(GET, "/login/**").permitAll()
                        .requestMatchers(POST, "/logout/**").authenticated()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        internalApiKeyFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        if (environment.matchesProfiles("local")) {
            http
                    .headers(headers ->
                            headers.frameOptions(FrameOptionsConfig::disable)
                    )
            ;
        }

        return http.build();
    }

    private String internalPathPattern() {
        String prefix = internalApiProperties.pathPrefix();

        if (prefix.endsWith("/")) {
            return prefix + "**";
        }

        return prefix + "/**";
    }
}
