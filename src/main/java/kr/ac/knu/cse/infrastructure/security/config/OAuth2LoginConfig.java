package kr.ac.knu.cse.infrastructure.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.util.StringUtils;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class OAuth2LoginConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;
    @Value("${app.oauth2.idp-hint:google}")
    private String idpHint;

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver() {
        DefaultOAuth2AuthorizationRequestResolver resolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        "/oauth2/authorization"
                );

        resolver.setAuthorizationRequestCustomizer(customizer ->
                customizer.additionalParameters(params -> {
                    if (StringUtils.hasText(idpHint)) {
                        params.put("kc_idp_hint", idpHint);
                    }
                })
        );

        return resolver;
    }

}
