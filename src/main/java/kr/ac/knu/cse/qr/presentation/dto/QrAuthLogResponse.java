package kr.ac.knu.cse.qr.presentation.dto;

import java.time.LocalDate;

public record QrAuthLogResponse(
	Long qrAuthLogId,
	LocalDate scanDate,
	String studentNumber,
	String studentName,
	boolean duesPaid,
	String scannedBy
) {
}
