package kr.ac.knu.cse.provider.persistence;

import kr.ac.knu.cse.provider.domain.Provider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long>, ProviderQueryDslRepository {
	@EntityGraph(attributePaths = "student")
	Optional<Provider> findByEmail(String email);
}
