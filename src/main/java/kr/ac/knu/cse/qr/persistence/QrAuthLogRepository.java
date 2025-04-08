package kr.ac.knu.cse.qr.persistence;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.ac.knu.cse.qr.domain.QrAuthLog;

@Repository
public interface QrAuthLogRepository extends JpaRepository<QrAuthLog, Long> {
	boolean existsByScanDateAndStudentNumberAndScannedBy(
		LocalDate scanDate, String studentNumber, String scannedBy);
}
