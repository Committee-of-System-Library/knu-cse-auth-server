package kr.ac.knu.cse.admin.application;

import kr.ac.knu.cse.admin.presentation.dto.AdminStatisticsResponse;
import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.qr.persistence.QrAuthLogRepository;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStatisticsService {

    private final StudentRepository studentRepository;
    private final DuesRepository duesRepository;
    private final QrAuthLogRepository qrAuthLogRepository;
    private final ProviderRepository providerRepository;

    public AdminStatisticsResponse getStatistics() {
        // 전체 학생 수
        long totalStudents = studentRepository.count();

        // 회비 납부자 수 (Dues 테이블에 데이터가 있으면 납부)
        long paidDues = duesRepository.count();

        // QR 스캔 횟수
        long qrScans = qrAuthLogRepository.count();

        // 활성 Provider 수
        long providers = providerRepository.count();

        return new AdminStatisticsResponse(totalStudents, paidDues, qrScans, providers);
    }
}