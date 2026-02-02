package kr.ac.knu.cse.domain.student;

import static kr.ac.knu.cse.domain.role.RoleType.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudentTest {

    @DisplayName("Creates a student with ROLE_USER when name and student number are provided.")
    @Test
    void of() {
        //given && when
        Student student = Student.of("이름", "test@knu.ac.kr");

        //then
        assertThat(student.getId()).isNull();
        assertThat(student.getRole()).isEqualTo(ROLE_USER);
        assertThat(student.getMajor()).isEqualTo("UNKNOWN");
    }

    @DisplayName("Returns true when a major is provided.")
    @Test
    void hasConfirmedMajor() {
        //given
        Student student = Student.of("이름", "test@knu.ac.kr");
        student.replaceMajor("컴퓨터학부");

        //when && then
        assertThat(student.hasConfirmedMajor()).isTrue();
    }

    @DisplayName("Returns true when a student number is provided.")
    @Test
    void hasConfirmedStudentNumber() {
        //given
        Student student = Student.of("이름", "TEMP12345678910");
        student.replaceStudentNumber("2022111111");

        //when && then
        assertThat(student.hasConfirmedStudentNumber()).isTrue();
    }

    @DisplayName("Returns true when a grade is provided.")
    @Test
    void hasConfirmedGrade() {
        //given
        Student student = Student.of("이름", "TEMP12345678910");
        student.replaceGrade("2");

        //when && then
        assertThat(student.hasConfirmedGrade()).isTrue();
    }
}