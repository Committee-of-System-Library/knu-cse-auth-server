package kr.ac.knu.cse.domain.verification;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long> {

    Optional<VerificationRequest> findByStudentIdAndStatus(Long studentId, VerificationStatus status);

    boolean existsByStudentIdAndStatus(Long studentId, VerificationStatus status);

    List<VerificationRequest> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
