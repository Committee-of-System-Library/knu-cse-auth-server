package kr.ac.knu.cse.domain.due;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import kr.ac.knu.cse.support.JpaIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JpaIntegrationTest
class DuesRepositoryTest {

    @Autowired
    DuesRepository duesRepository;
    @Autowired
    EntityManager em;

    @DisplayName("Returns true when the student's is paid")
    @Test
    void existsByStudentIdAndIsPaidTrue() {
        //given
        Dues dues = Dues.of(
                1L,
                "2022111111",
                "4"
        );

        dues.pay();

        duesRepository.save(dues);

        em.flush();
        em.clear();

        //when && then
        assertThat(
                duesRepository.existsByStudentIdAndIsPaidTrue(1L))
                .isTrue();
    }
}