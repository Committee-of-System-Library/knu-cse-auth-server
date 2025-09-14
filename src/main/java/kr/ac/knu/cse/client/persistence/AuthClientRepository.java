package kr.ac.knu.cse.client.persistence;

import kr.ac.knu.cse.client.domain.AuthClient;
import kr.ac.knu.cse.client.domain.AuthClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthClientRepository extends JpaRepository<AuthClient, Long> {

	Optional<AuthClient> findByClientName(String clientName);

	Optional<AuthClient> findByClientIdAndStatus(Long clientId, AuthClientStatus status);

	List<AuthClient> findAllByStatus(AuthClientStatus status);

	@Query("SELECT c FROM AuthClient c JOIN c.allowedDomains d WHERE :redirectUrl LIKE CONCAT(d, '%') AND c.status = 'ACTIVE'")
	Optional<AuthClient> findByAllowedRedirectUrl(@Param("redirectUrl") String redirectUrl);

	boolean existsByClientName(String clientName);
}
