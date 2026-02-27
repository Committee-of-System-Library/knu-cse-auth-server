package kr.ac.knu.cse.domain.role;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoleChangeLogTest {

    @DisplayName("processedAt이 null인 로그를 생성한다.")
    @Test
    void of() {
        assertThat(RoleChangeLog.of(
                1L,
                RoleType.ROLE_USER,
                RoleType.ROLE_EXEC,
                LocalDateTime.now()).getProcessedAt()
        ).isNull();
    }

    @DisplayName("processedAt이 null이 아니면, true를 반환한다.")
    @Test
    void isProcessed() {
        //given
        RoleChangeLog log = RoleChangeLog.of(
                1L,
                RoleType.ROLE_USER,
                RoleType.ROLE_EXEC,
                LocalDateTime.now());

        log.markProcessed();

        //when && then
        assertThat(log.isProcessed()).isTrue();
    }
}