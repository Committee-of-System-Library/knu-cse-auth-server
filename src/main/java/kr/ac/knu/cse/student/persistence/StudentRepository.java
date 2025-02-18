package kr.ac.knu.cse.student.persistence;

import kr.ac.knu.cse.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
