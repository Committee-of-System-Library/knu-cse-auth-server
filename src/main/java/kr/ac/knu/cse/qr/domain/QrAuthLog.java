package kr.ac.knu.cse.qr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.ac.knu.cse.global.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 학생증 QR 인증 로그 테이블
 */
@Getter
@Entity
@Table(name = "qr_auth_log",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_qr_auth_log", columnNames = {"scan_date", "student_number", "scanned_by"})
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QrAuthLog extends BaseEntity {

	@Id
	@Column(name = "qr_auth_log_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scan_date", nullable = false)
	private LocalDate scanDate;

	@Column(name = "student_number", length = 15, nullable = false)
	private String studentNumber;

	@Column(name = "student_name", length = 50, nullable = false)
	private String studentName;

	@Column(name = "dues_paid", nullable = false)
	private boolean duesPaid;

	// 스캔을 진행한 관리자/집행부/재정부 계정 이메일
	@Column(name = "scanned_by", length = 255, nullable = false)
	private String scannedBy;

	@Builder
	public QrAuthLog(LocalDate scanDate, String studentNumber, String studentName, boolean duesPaid, String scannedBy) {
		this.scanDate = scanDate;
		this.studentNumber = studentNumber;
		this.studentName = studentName;
		this.duesPaid = duesPaid;
		this.scannedBy = scannedBy;
	}
}
