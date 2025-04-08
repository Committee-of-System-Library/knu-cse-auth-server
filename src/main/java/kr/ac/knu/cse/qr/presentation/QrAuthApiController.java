package kr.ac.knu.cse.qr.presentation;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.knu.cse.dues.exception.DuesNotFoundException;
import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.qr.application.QrAuthLogService;
import kr.ac.knu.cse.qr.application.dto.QrAuthLogDto;
import kr.ac.knu.cse.qr.presentation.dto.SaveQrAuthLogsReq;
import kr.ac.knu.cse.security.annotation.LoggedInProvider;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.exception.StudentNotFoundException;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/qr-auth/api")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
public class QrAuthApiController {

	private final StudentRepository studentRepository;
	private final DuesRepository duesRepository;
	private final QrAuthLogService qrAuthLogService;

	@GetMapping("/student")
	public ApiSuccessResult<?> checkStudent(
		@RequestParam String studentNumber,
		@RequestParam(defaultValue = "false") boolean duesOnly
	) {
		Student student = studentRepository.findByStudentNumber(studentNumber)
			.orElseThrow(StudentNotFoundException::new);

		if (duesOnly) {
			boolean hasDues = duesRepository.findByStudent(student).isPresent();
			if (!hasDues) {
				throw new DuesNotFoundException();
			}
		}

		return ApiResponse.success(HttpStatus.OK, Map.of(
			"studentNumber", student.getStudentNumber(),
			"studentName", student.getName(),
			"duesPaid", duesOnly
		));
	}

	@PostMapping("/logs")
	@PreAuthorize("isAuthenticated()")
	public ApiSuccessResult<?> saveQrAuthLogs(
		@LoggedInProvider PrincipalDetails principalDetails,
		@RequestBody SaveQrAuthLogsReq body
	) {
		String scannedBy = principalDetails.getName();
		LocalDate today = LocalDate.now();

		List<QrAuthLogDto> dtos = body.scannedStudents().stream()
			.map(s -> new QrAuthLogDto(s.studentNumber(), s.studentName(), s.duesPaid()))
			.toList();

		qrAuthLogService.saveLogs(today, scannedBy, dtos);

		return ApiResponse.success(HttpStatus.OK, "QR 스캔 로그가 저장되었습니다.");
	}
}
