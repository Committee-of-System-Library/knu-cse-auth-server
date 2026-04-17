package kr.ac.knu.cse.domain.snack;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnackHandoutRepository extends JpaRepository<SnackHandout, Long> {

    List<SnackHandout> findAllByEventIdOrderByReceivedAtAsc(Long eventId);

    boolean existsByEventIdAndStudentNumber(Long eventId, String studentNumber);

    long countByEventId(Long eventId);
}
