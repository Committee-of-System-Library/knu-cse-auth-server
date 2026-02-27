package kr.ac.knu.cse.application.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuthUserInfo {

    private final String providerName;
    private final String providerKey;
    private final String email;
    private final String name;

    public static OAuthUserInfo of(
            String providerName,
            String providerKey,
            String email,
            String name
    ) {
        validate(providerName, providerKey, email, name);
        return new OAuthUserInfo(providerName, providerKey, email, name);
    }

    private static void validate(
            String providerName,
            String providerKey,
            String email,
            String name
    ) {
        validateProviderName(providerName);
        validateProviderKey(providerKey);
        validateEmail(email);
        validateName(name);
    }

    private static void validateProviderName(String providerName) {
        if (providerName == null || providerName.isBlank()) {
            throw new IllegalArgumentException("providerName must be assigned");
        }
    }

    private static void validateProviderKey(String providerKey) {
        if (providerKey == null || providerKey.isBlank()) {
            throw new IllegalArgumentException("providerKey must be assigned");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must be assigned");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must be assigned");
        }
    }
}
