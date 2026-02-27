package kr.ac.knu.cse.domain.due;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DuesRepository extends JpaRepository<Dues, Long> {

    boolean existsByStudentIdAndIsPaidTrue(Long studentId);
}
