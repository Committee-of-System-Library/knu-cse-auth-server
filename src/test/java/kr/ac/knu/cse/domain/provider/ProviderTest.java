package kr.ac.knu.cse.domain.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProviderTest {

    @DisplayName("Creates a Provider with provider name, provider key, and email.")
    @Test
    void of() {
        //given && when
        Provider provider = Provider.of("test@knu.ac.kr", "google", "provider_key", 1L);

        //then
        assertThat(provider.getId()).isNull();
    }
}