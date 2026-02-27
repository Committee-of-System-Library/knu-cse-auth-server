package kr.ac.knu.cse.domain.redirect;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kr.ac.knu.cse.global.exception.auth.InvalidRedirectUriException;
import kr.ac.knu.cse.support.SpringBootTestWithoutSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTestWithoutSecurity
class RedirectUriPolicyTest {

    @Autowired
    RedirectUriPolicy redirectUriPolicy;

    @DisplayName("redirect_uri가 명시되지 않은 경우, 예외가 발생한다.")
    @Test
    void validate_have_not_assigned() {
        assertThatThrownBy(() -> redirectUriPolicy.validate(""))
                .isInstanceOf(InvalidRedirectUriException.class);

    }
}