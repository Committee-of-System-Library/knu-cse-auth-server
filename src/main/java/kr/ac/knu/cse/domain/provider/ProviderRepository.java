package kr.ac.knu.cse.domain.provider;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByProviderNameAndProviderKey(
            String providerName,
            String providerKey
    );

    Optional<Provider> findByStudentId(Long studentId);
}
