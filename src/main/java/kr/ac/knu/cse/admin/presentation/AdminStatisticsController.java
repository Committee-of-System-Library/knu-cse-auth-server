package kr.ac.knu.cse.admin.presentation;

import kr.ac.knu.cse.admin.application.AdminStatisticsService;
import kr.ac.knu.cse.admin.presentation.dto.AdminStatisticsResponse;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('FINANCE')")
@Slf4j
public class AdminStatisticsController {

    private final AdminStatisticsService adminStatisticsService;

    @GetMapping("/statistics")
    public ResponseEntity<ApiSuccessResult<AdminStatisticsResponse>> getStatistics() {
        log.info("관리자 통계 조회 요청");
        AdminStatisticsResponse statistics = adminStatisticsService.getStatistics();
        log.info("관리자 통계 조회 완료");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, statistics));
    }
}
