package kr.ac.knu.cse.qr.persistence;

import kr.ac.knu.cse.qr.domain.QrAuthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface QrAuthLogRepository extends JpaRepository<QrAuthLog, Long> {
    boolean existsByScanDateAndStudentNumberAndScannedBy(
            LocalDate scanDate, String studentNumber, String scannedBy);
}
