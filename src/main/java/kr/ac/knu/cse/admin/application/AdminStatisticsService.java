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
        long totalStudents = studentRepository.count();

        long paidDues = duesRepository.count();

        long qrScans = qrAuthLogRepository.count();

        long providers = providerRepository.count();

        return new AdminStatisticsResponse(totalStudents, paidDues, qrScans, providers);
    }
}