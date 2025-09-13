package kr.ac.knu.cse.dues.persistence;

import kr.ac.knu.cse.dues.domain.Dues;
import kr.ac.knu.cse.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DuesRepository extends JpaRepository<Dues, Long>, DuesQueryDslRepository {
	Optional<Dues> findByStudent(Student student);
}
