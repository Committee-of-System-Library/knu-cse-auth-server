package kr.ac.knu.cse.admin.application;

import kr.ac.knu.cse.admin.presentation.dto.AdminStatisticsResponse;
import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.qr.persistence.QrAuthLogRepository;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminStatisticsService {

    private final StudentRepository studentRepository;
    private final DuesRepository duesRepository;
    private final QrAuthLogRepository qrAuthLogRepository;
    private final ProviderRepository providerRepository;

    public AdminStatisticsResponse getStatistics() {
        log.info("관리자 통계 조회");
        long totalStudents = studentRepository.count();
        log.debug("총 학생 수: {}", totalStudents);

        long paidDues = duesRepository.count();
        log.debug("학생회비 납부자 수: {}", paidDues);

        long qrScans = qrAuthLogRepository.count();
        log.debug("QR 스캔 수: {}", qrScans);

        long providers = providerRepository.count();
        log.debug("연동된 서비스 수: {}", providers);

        return new AdminStatisticsResponse(totalStudents, paidDues, qrScans, providers);
    }
}
