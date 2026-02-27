package kr.ac.knu.cse.domain.student;

import static kr.ac.knu.cse.domain.role.RoleType.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kr.ac.knu.cse.global.exception.provisioning.InvalidRoleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudentTest {

    @DisplayName("Creates a student with ROLE_USER.")
    @Test
    void of() {
        //given && when
        Student student = Student.of(
                "컴퓨터학부",
                "이름",
                "2022111111",
                Grade.SECOND
        );

        //then
        assertThat(student.getId()).isNull();
        assertThat(student.getRole()).isEqualTo(ROLE_USER);
    }


    @DisplayName("Throws an InvalidRoleException when invalid role is provided.")
    @Test
    void grantRole() {
        //given
        Student student = Student.of(
                "컴퓨터학부",
                "이름",
                "2022111111",
                Grade.SECOND
        );

        //when && then
        assertThatThrownBy(() -> student.grantRole(null))
                .isInstanceOf(InvalidRoleException.class);
    }
}