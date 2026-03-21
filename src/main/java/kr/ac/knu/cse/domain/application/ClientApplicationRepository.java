package kr.ac.knu.cse.domain.application;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientApplicationRepository extends JpaRepository<ClientApplication, Long> {

    List<ClientApplication> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<ClientApplication> findByStatusOrderByCreatedAtAsc(ClientApplicationStatus status);

    List<ClientApplication> findAllByOrderByCreatedAtDesc();

    Optional<ClientApplication> findByClientId(String clientId);
}
