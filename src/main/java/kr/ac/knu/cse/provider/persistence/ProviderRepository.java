package kr.ac.knu.cse.provider.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.ac.knu.cse.provider.domain.Provider;

public interface ProviderRepository extends JpaRepository<Provider, String> {
	@EntityGraph(attributePaths = "student")
	Optional<Provider> findByEmail(String email);
}
