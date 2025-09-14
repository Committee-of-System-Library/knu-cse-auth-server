package kr.ac.knu.cse.provider.persistence;

import kr.ac.knu.cse.provider.presentation.dto.ProviderResponse;
import kr.ac.knu.cse.provider.presentation.dto.ProviderSearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProviderQueryDslRepository {
    Page<ProviderResponse> findProviders(ProviderSearchFilter filter, Pageable pageable);
}
