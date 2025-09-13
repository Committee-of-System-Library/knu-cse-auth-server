package kr.ac.knu.cse.qr.application;

import kr.ac.knu.cse.qr.application.dto.QrAuthLogDto;
import kr.ac.knu.cse.qr.domain.QrAuthLog;
import kr.ac.knu.cse.qr.persistence.QrAuthLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrAuthLogService {

	private final QrAuthLogRepository qrAuthLogRepository;

	@Transactional
	public void saveLogs(LocalDate scanDate, String scannedBy, List<QrAuthLogDto> logs) {
		for (QrAuthLogDto dto : logs) {
			boolean alreadyExists = qrAuthLogRepository.existsByScanDateAndStudentNumberAndScannedBy(
				scanDate, dto.studentNumber(), scannedBy
			);
			if (alreadyExists) {
				log.warn("이미 저장된 로그 (중복) => studentNumber={}, scannedBy={}, date={}",
					dto.studentNumber(), scannedBy, scanDate);
				continue;
			}

			QrAuthLog logEntity = QrAuthLog.builder()
				.scanDate(scanDate)
				.scannedBy(scannedBy)
				.studentNumber(dto.studentNumber())
				.studentName(dto.studentName())
				.duesPaid(dto.duesPaid())
				.build();

			qrAuthLogRepository.save(logEntity);
		}
	}

}
