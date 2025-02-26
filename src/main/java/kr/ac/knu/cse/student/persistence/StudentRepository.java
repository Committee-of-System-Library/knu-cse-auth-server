package kr.ac.knu.cse.student.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.ac.knu.cse.student.domain.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByStudentNumber(String studentNumber);
}
