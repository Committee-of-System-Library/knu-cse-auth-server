package kr.ac.knu.cse.domain.redirect;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedirectUriPolicy {

    private final RedirectUriProperties redirectUriProperties;

    public void validate(String redirectUri) {
        if (redirectUri == null || redirectUri.isBlank()) {
            throw new IllegalArgumentException("redirect_uri is required");
        }

        if (!redirectUriProperties.redirectAllowlist().contains(redirectUri)) {
            throw new IllegalArgumentException("redirect_uri is not allowed");
        }
    }
}
