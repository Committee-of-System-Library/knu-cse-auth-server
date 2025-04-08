package kr.ac.knu.cse.qr.presentation;

import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.qr.persistence.QrAuthLogRepository;
import kr.ac.knu.cse.qr.presentation.dto.QrAuthLogSearchFilter;
import kr.ac.knu.cse.qr.presentation.dto.QrAuthLogResponse;
import kr.ac.knu.cse.qr.persistence.QrAuthLogQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage/qr-auth")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('FINANCE')")
public class QrAuthLogManageController {

	private final QrAuthLogRepository qrAuthLogRepository;
	private final QrAuthLogQueryDslRepository qrAuthLogQueryDslRepository;

	@GetMapping
	public ApiSuccessResult<Page<QrAuthLogResponse>> getQrAuthLogs(
		@ModelAttribute QrAuthLogSearchFilter filter,
		Pageable pageable
	) {
		Page<QrAuthLogResponse> page = qrAuthLogQueryDslRepository.findQrAuthLogs(filter, pageable);
		return ApiResponse.success(HttpStatus.OK, page);
	}

	@DeleteMapping("/{id}")
	public ApiSuccessResult<?> deleteQrAuthLog(@PathVariable("id") Long id) {
		if (!qrAuthLogRepository.existsById(id)) {
			return ApiResponse.success(HttpStatus.NOT_FOUND, "해당 로그가 존재하지 않습니다.");
		}
		qrAuthLogRepository.deleteById(id);
		return ApiResponse.success(HttpStatus.OK, "QR 로그가 삭제되었습니다.");
	}
}
