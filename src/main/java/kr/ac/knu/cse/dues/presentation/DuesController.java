package kr.ac.knu.cse.dues.presentation;

import kr.ac.knu.cse.dues.application.DuesCommandService;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.security.annotation.LoggedInProvider;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.student.domain.Student;
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
@RequestMapping("/dues")
@Slf4j
public class DuesController {
    private final DuesCommandService duesCommandService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiSuccessResult<Boolean>> getMyDues(
            @LoggedInProvider PrincipalDetails principalDetails
    ) {
        Student student = principalDetails.student();
        log.info("학생회비 납부 정보 조회 요청 - 학생 ID: {}", student.getId());
        boolean hasDues = duesCommandService.checkMyDues(student);
        log.info("학생회비 납부 정보 조회 완료 - 학생 ID: {}, 납부 여부: {}", student.getId(), hasDues);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, hasDues));
    }
}
