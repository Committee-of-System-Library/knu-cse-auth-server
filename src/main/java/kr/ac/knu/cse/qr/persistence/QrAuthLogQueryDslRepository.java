package kr.ac.knu.cse.qr.persistence;

import kr.ac.knu.cse.qr.presentation.dto.QrAuthLogResponse;
import kr.ac.knu.cse.qr.presentation.dto.QrAuthLogSearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QrAuthLogQueryDslRepository {
	Page<QrAuthLogResponse> findQrAuthLogs(QrAuthLogSearchFilter filter, Pageable pageable);
}
