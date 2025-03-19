package kr.ac.knu.cse.dues.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.ac.knu.cse.dues.domain.Dues;
import kr.ac.knu.cse.student.domain.Student;

public interface DuesRepository extends JpaRepository<Dues, Long> {
	Optional<Dues> findByStudent(Student student);
}
