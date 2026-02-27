package kr.ac.knu.cse.domain.due;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DuesTest {

    @DisplayName("Dues are created with the student dues payment status set to false.")
    @Test
    void of() {
        assertThat(
                Dues.of(
                        1L,
                        "2022111111",
                        "4"
                ).isPaid()
        ).isFalse();
    }
}