package kr.ac.knu.cse.domain.client;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthClientRepository extends JpaRepository<AuthClient, Long> {

    Optional<AuthClient> findByClientName(String clientName);
}
