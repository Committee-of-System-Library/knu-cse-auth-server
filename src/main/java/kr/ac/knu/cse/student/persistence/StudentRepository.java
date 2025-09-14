package kr.ac.knu.cse.student.persistence;

import kr.ac.knu.cse.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long>, StudentQueryDslRepository {

    boolean existsByStudentNumber(String studentNumber);

    Optional<Student> findByStudentNumber(String studentNumber);
}
