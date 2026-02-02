package kr.ac.knu.cse.application.auth.dto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kr.ac.knu.cse.application.auth.dto.OAuthUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthUserInfoTest {

    @DisplayName("Throws an exception if any of providerName, providerKey, email, or name is not provided.")
    @Test
    void of() {
        assertThatThrownBy(() -> OAuthUserInfo.of(
                null,
                "key",
                "test@knu.ac.kr",
                "name")
        ).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> OAuthUserInfo.of(
                "providerName",
                "",
                "test@knu.ac.kr",
                "name")
        ).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> OAuthUserInfo.of(
                "providerName",
                "key",
                null,
                "name")
        ).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> OAuthUserInfo.of(
                "providerName",
                "key",
                "test@knu.ac.kr",
                "")
        ).isInstanceOf(IllegalArgumentException.class);
    }
}