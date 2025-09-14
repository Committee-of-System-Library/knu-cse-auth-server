package kr.ac.knu.cse.auth.presentation;

import jakarta.servlet.http.HttpServletResponse;
import kr.ac.knu.cse.auth.application.AuthorizationCodeService;
import kr.ac.knu.cse.auth.application.TempAuthStorage;
import kr.ac.knu.cse.auth.exception.SessionExpiredException;
import kr.ac.knu.cse.auth.presentation.dto.AdditionalInfoResponse;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.provider.application.ProviderService;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.student.application.StudentService;
import kr.ac.knu.cse.student.domain.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/additional-info")
@RequiredArgsConstructor
public class AdditionalInfoController {

    private final StudentService studentService;
    private final ProviderService providerService;
    private final TempAuthStorage tempAuthStorage;
    private final AuthorizationCodeService authorizationCodeService;

    @GetMapping("/check")
    public ResponseEntity<ApiSuccessResult<AdditionalInfoResponse>> checkStudentNumber(@RequestParam("studentNumber") String studentNumber) {
        Student student = studentService.getStudentByStudentNumber(studentNumber);
        AdditionalInfoResponse response = AdditionalInfoResponse.of(student);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, response));

    }

    @PostMapping("/connect")
    public void connectStudent(
            @RequestBody Map<String, String> body,
            HttpServletResponse response
    ) throws IOException {
        String tempToken = body.get("token");
        String studentNumber = body.get("studentNumber");
        String redirectUrl = body.get("redirectUrl");

        log.info("[DEBUG] /connect 요청 - 임시 토큰: {}", tempToken);
        log.info("[DEBUG] 학번: {}", studentNumber);
        log.info("[DEBUG] Redirect URL: {}", redirectUrl);

        if (tempToken == null || tempToken.isBlank()) {
            log.error("[DEBUG] 임시 토큰이 요청에 없습니다.");
            throw new SessionExpiredException();
        }

        if (redirectUrl == null || redirectUrl.isBlank()) {
            log.error("[DEBUG] Redirect URL이 요청에 없습니다.");
            throw new SessionExpiredException();
        }

        TempAuthStorage.TempAuthData authData = tempAuthStorage.retrieveAndRemove(tempToken);
        if (authData == null) {
            log.error("[DEBUG] 유효하지 않거나 만료된 임시 토큰: {}", tempToken);
            throw new SessionExpiredException();
        }

        PrincipalDetails principalDetails = authData.principalDetails();

        log.info("[DEBUG] 임시 토큰 검증 성공. 사용자: {}", principalDetails.getName());

        String email = principalDetails.getName();
        Student student = studentService.getStudentByStudentNumber(studentNumber);
        providerService.connectStudent(email, student);

        log.info("[DEBUG] 학생 연결 완료. Authorization Code를 발급합니다.");

        String authorizationCode = authorizationCodeService.generateCode(email, redirectUrl);
        log.info("사용자({})에게 Authorization Code 발급 완료", email);

        String finalRedirectUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("code", authorizationCode)
                .build().toUriString();

        log.info("최종 목적지인 {}로 리다이렉트합니다.", finalRedirectUrl);

        response.sendRedirect(finalRedirectUrl);
    }
}
