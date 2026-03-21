package kr.ac.knu.cse.domain.registry;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CseStudentRegistryRepository extends JpaRepository<CseStudentRegistry, Long> {

    Optional<CseStudentRegistry> findByStudentNumber(String studentNumber);

    boolean existsByStudentNumber(String studentNumber);
}
