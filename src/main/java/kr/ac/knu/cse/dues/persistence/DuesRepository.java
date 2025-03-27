package kr.ac.knu.cse.dues.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.ac.knu.cse.dues.domain.Dues;
import kr.ac.knu.cse.student.domain.Student;

@Repository
public interface DuesRepository extends JpaRepository<Dues, Long>, DuesQueryDslRepository {
	Optional<Dues> findByStudent(Student student);
}
