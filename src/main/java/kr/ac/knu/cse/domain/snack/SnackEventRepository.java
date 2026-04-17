package kr.ac.knu.cse.domain.snack;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnackEventRepository extends JpaRepository<SnackEvent, Long> {

    List<SnackEvent> findAllByOrderByOpenedAtDesc();

    List<SnackEvent> findAllByStatusOrderByOpenedAtDesc(SnackEventStatus status);
}
