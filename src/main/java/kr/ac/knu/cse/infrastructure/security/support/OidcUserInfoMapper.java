package kr.ac.knu.cse.infrastructure.security.support;

import kr.ac.knu.cse.application.dto.OAuthUserInfo;
import kr.ac.knu.cse.global.exception.auth.InvalidEmailDomainException;
import kr.ac.knu.cse.global.exception.auth.InvalidOidcUserException;
import kr.ac.knu.cse.global.exception.auth.MissingEmailClaimException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class OidcUserInfoMapper {

    @Value("${app.auth.allowed-email-domain:@knu.ac.kr}")
    private String requiredEmailDomain;
    private static final String PROVIDER_NAME = "KEYCLOAK";


    public OAuthUserInfo map(OidcUser oidcUser) {
        validateOidcUser(oidcUser);

        String providerKey = extractProviderKey(oidcUser);
        String name = extractName(oidcUser);

        String email = extractEmail(oidcUser);
        validateEmailDomain(email);

        return OAuthUserInfo.of(
                PROVIDER_NAME,
                providerKey,
                email,
                name
        );
    }

    private void validateOidcUser(OidcUser oidcUser) {
        if (oidcUser == null) {
            throw new InvalidOidcUserException();
        }
    }

    private String extractProviderKey(OidcUser oidcUser) {
        String providerKey = oidcUser.getSubject();

        if (providerKey == null || providerKey.isBlank()) {
            throw new InvalidOidcUserException();
        }

        return providerKey;
    }

    private String extractEmail(OidcUser oidcUser) {
        String email = oidcUser.getEmail();

        if (email == null || email.isBlank()) {
            throw new MissingEmailClaimException();
        }

        return email;
    }

    private String extractName(OidcUser oidcUser) {
        String name = oidcUser.getFullName();

        if (name != null && !name.isBlank()) {
            return name;
        }

        String preferredUsername = oidcUser.getPreferredUsername();

        if (preferredUsername == null || preferredUsername.isBlank()) {
            throw new InvalidOidcUserException();
        }

        return preferredUsername;
    }

    private void validateEmailDomain(String email) {
        if (!email.toLowerCase().endsWith(requiredEmailDomain.toLowerCase())) {
            throw new InvalidEmailDomainException();
        }
    }
}
