package kr.ac.knu.cse.domain.role;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleChangeLogRepository extends JpaRepository<RoleChangeLog, Long> {
    List<RoleChangeLog> findTop100ByProcessedAtIsNullOrderByChangedAtAsc();
}
