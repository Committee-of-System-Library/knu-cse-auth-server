package kr.ac.knu.cse.domain.role;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import kr.ac.knu.cse.support.JpaIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JpaIntegrationTest
class RoleChangeLogRepositoryTest {

    @Autowired
    RoleChangeLogRepository roleChangeLogRepository;
    @Autowired
    EntityManager em;

    @DisplayName("processedAt이 null인 아직 처리되지 않은 로그들을 조회한다.")
    @Test
    void findTop100ByProcessedAtIsNullOrderByChangedAtAsc_UnProcessed() {
        //given
        roleChangeLogRepository.save(RoleChangeLog.of(
                1L,
                RoleType.ROLE_USER,
                RoleType.ROLE_MEMBER,
                LocalDateTime.now()
        ));

        em.flush();
        em.clear();

        //when && then
        Assertions.assertThat(
                roleChangeLogRepository.findTop100ByProcessedAtIsNullOrderByChangedAtAsc().size()
        ).isEqualTo(1);
    }

    @DisplayName("최대 100 개의 로그들만을 조회한다.")
    @Test
    void findTop100ByProcessedAtIsNullOrderByChangedAtAsc_Maximum_100() {
        //given
        for (long i = 1; i <= 101; i++) {
            roleChangeLogRepository.save(RoleChangeLog.of(
                    i,
                    RoleType.ROLE_USER,
                    RoleType.ROLE_MEMBER,
                    LocalDateTime.now()
            ));
        }

        em.flush();
        em.clear();

        //when && then
        Assertions.assertThat(
                roleChangeLogRepository.findTop100ByProcessedAtIsNullOrderByChangedAtAsc().size()
        ).isEqualTo(100);
    }
}