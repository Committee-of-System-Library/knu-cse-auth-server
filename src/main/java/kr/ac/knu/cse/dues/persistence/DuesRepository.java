package kr.ac.knu.cse.dues.persistence;

import kr.ac.knu.cse.dues.domain.Dues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DuesRepository extends JpaRepository<Dues, Long> {
}
