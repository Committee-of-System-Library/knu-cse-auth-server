package kr.ac.knu.cse.domain.student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import kr.ac.knu.cse.support.JpaIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JpaIntegrationTest
class StudentRepositoryTest {

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    EntityManager em;

    void flushAndClear() {
        em.flush();
        em.clear();
    }

    @DisplayName("Throws an exception at persistence time when creating a student with a duplicate student number.")
    @Test
    void of() {
        //given
        String studentNumber = "2022111111";
        Student student1 = Student.of("학생1",  studentNumber);

       studentRepository.save(student1);
        flushAndClear();

        Student student2 = Student.of("학생2", studentNumber);

        //when && then
        assertThatThrownBy(() -> {
            studentRepository.save(student2);
            flushAndClear();
        });
    }

    @DisplayName("Finds a student by student number.")
    @Test
    void findByStudentNumber() {
        //given
        String studentNumber = "2022111111";
        Student student = Student.of("학생1",  studentNumber);

        Student saved = studentRepository.save(student);
        flushAndClear();

        //when
        Optional<Student> result = studentRepository.findByStudentNumber(studentNumber);

        //then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(saved);
    }

    @DisplayName("Returns true if a student with the given student number exists.")
    @Test
    void existsByStudentNumber() {
        //given
        String studentNumber = "2022111111";
        Student student = Student.of("학생1",  studentNumber);

        studentRepository.save(student);
        flushAndClear();

        //when && then
        assertThat(
                studentRepository.existsByStudentNumber(studentNumber)
        ).isTrue();
    }
}