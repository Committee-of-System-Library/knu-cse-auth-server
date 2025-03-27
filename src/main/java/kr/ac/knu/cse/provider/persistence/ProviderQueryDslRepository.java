package kr.ac.knu.cse.provider.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.ac.knu.cse.provider.presentation.dto.ProviderResponse;
import kr.ac.knu.cse.provider.presentation.dto.ProviderSearchFilter;

public interface ProviderQueryDslRepository {
	Page<ProviderResponse> findProviders(ProviderSearchFilter filter, Pageable pageable);
}
