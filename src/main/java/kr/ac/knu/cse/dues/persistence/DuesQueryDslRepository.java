package kr.ac.knu.cse.dues.persistence;

import kr.ac.knu.cse.dues.presentation.dto.DuesListResponse;
import kr.ac.knu.cse.dues.presentation.dto.DuesSearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DuesQueryDslRepository {
    Page<DuesListResponse> findDuesList(DuesSearchFilter filter, Pageable pageable);
}
