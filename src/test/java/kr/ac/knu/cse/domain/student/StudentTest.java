package kr.ac.knu.cse.domain.student;

import static kr.ac.knu.cse.domain.role.Role.STUDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kr.ac.knu.cse.global.exception.provisioning.InvalidRoleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudentTest {

    @DisplayName("Creates a student with STUDENT role and CSE_STUDENT type.")
    @Test
    void of() {
        //given && when
        Student student = Student.of(
                "컴퓨터학부",
                "이름",
                "2022111111",
                Gender.BLANK,
                UserType.CSE_STUDENT,
                STUDENT
        );

        //then
        assertThat(student.getId()).isNull();
        assertThat(student.getRole()).isEqualTo(STUDENT);
        assertThat(student.getUserType()).isEqualTo(UserType.CSE_STUDENT);
    }


    @DisplayName("Throws an InvalidRoleException when invalid role is provided.")
    @Test
    void grantRole() {
        //given
        Student student = Student.of(
                "컴퓨터학부",
                "이름",
                "2022111111",
                Gender.BLANK,
                UserType.CSE_STUDENT,
                STUDENT
        );

        //when && then
        assertThatThrownBy(() -> student.grantRole(null))
                .isInstanceOf(InvalidRoleException.class);
    }
}
