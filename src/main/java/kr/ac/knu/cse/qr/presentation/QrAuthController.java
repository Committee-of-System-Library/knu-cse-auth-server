package kr.ac.knu.cse.qr.presentation;

import kr.ac.knu.cse.dues.exception.DuesNotFoundException;
import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.qr.application.QrAuthLogService;
import kr.ac.knu.cse.qr.application.dto.QrAuthLogDto;
import kr.ac.knu.cse.qr.persistence.QrAuthLogQueryDslRepository;
import kr.ac.knu.cse.qr.persistence.QrAuthLogRepository;
import kr.ac.knu.cse.qr.presentation.dto.QrAuthLogResponse;
import kr.ac.knu.cse.qr.presentation.dto.QrAuthLogSearchFilter;
import kr.ac.knu.cse.qr.presentation.dto.SaveQrAuthLogsReq;
import kr.ac.knu.cse.security.annotation.LoggedInProvider;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.exception.StudentNotFoundException;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/qr-auth")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
public class QrAuthController {

	private final StudentRepository studentRepository;
	private final DuesRepository duesRepository;
	private final QrAuthLogService qrAuthLogService;
	private final QrAuthLogRepository qrAuthLogRepository;
	private final QrAuthLogQueryDslRepository qrAuthLogQueryDslRepository;

	@GetMapping("/student")
	public ResponseEntity<ApiSuccessResult<?>> checkStudent(
		@RequestParam String studentNumber,
		@RequestParam(defaultValue = "false") boolean duesOnly
	) {
		Student student = studentRepository.findByStudentNumber(studentNumber)
			.orElseThrow(StudentNotFoundException::new);

		// 실제 회비 납부 여부 확인
		boolean hasDues = duesRepository.findByStudent(student).isPresent();

		if (duesOnly && !hasDues) {
			throw new DuesNotFoundException();
		}

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, Map.of(
				"studentNumber", student.getStudentNumber(),
				"studentName", student.getName(),
				"duesPaid", hasDues
			)));
	}

	@PostMapping("/logs")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiSuccessResult<?>> saveQrAuthLogs(
		@LoggedInProvider PrincipalDetails principalDetails,
		@RequestBody SaveQrAuthLogsReq body
	) {
		String scannedBy = principalDetails.getName();
		LocalDate today = LocalDate.now();

		List<QrAuthLogDto> dtos = body.scannedStudents().stream()
			.map(s -> new QrAuthLogDto(s.studentNumber(), s.studentName(), s.duesPaid()))
			.toList();

		qrAuthLogService.saveLogs(today, scannedBy, dtos);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, "QR 스캔 로그가 저장되었습니다."));
	}

	@GetMapping("/logs")
	public ResponseEntity<ApiSuccessResult<Page<QrAuthLogResponse>>> getQrAuthLogs(
		@ModelAttribute QrAuthLogSearchFilter filter,
		Pageable pageable
	) {
		Page<QrAuthLogResponse> page = qrAuthLogQueryDslRepository.findQrAuthLogs(filter, pageable);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, page));
	}

	@DeleteMapping("/logs/{id}")
	public ResponseEntity<ApiSuccessResult<?>> deleteQrAuthLog(@PathVariable("id") Long id) {
		if (!qrAuthLogRepository.existsById(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.success(HttpStatus.NOT_FOUND, "해당 로그가 존재하지 않습니다."));
		}
		qrAuthLogRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, "QR 로그가 삭제되었습니다."));
	}
}
