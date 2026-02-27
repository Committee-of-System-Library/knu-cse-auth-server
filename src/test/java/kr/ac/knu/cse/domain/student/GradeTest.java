package kr.ac.knu.cse.domain.student;

import static kr.ac.knu.cse.domain.student.Grade.FIRST;
import static kr.ac.knu.cse.domain.student.Grade.FOURTH;
import static kr.ac.knu.cse.domain.student.Grade.OTHERS;
import static kr.ac.knu.cse.domain.student.Grade.SECOND;
import static kr.ac.knu.cse.domain.student.Grade.THIRD;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GradeTest {

    @DisplayName("promote grade.")
    @Test
    void promote() {
        Assertions.assertThat(FIRST.promote()).isEqualTo(SECOND);
        Assertions.assertThat(SECOND.promote()).isEqualTo(THIRD);
        Assertions.assertThat(THIRD.promote()).isEqualTo(FOURTH);
        Assertions.assertThat(FOURTH.promote()).isEqualTo(OTHERS);
        Assertions.assertThat(OTHERS.promote()).isEqualTo(OTHERS);
    }
}