package kr.ac.knu.cse.auth.presentation;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.knu.cse.auth.applicaiton.TokenRedirectProvider;
import kr.ac.knu.cse.provider.application.ProviderService;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.student.application.StudentService;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.exception.StudentNotFoundException;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdditionalInfoController {

	private final StudentService studentService;
	private final ProviderService providerService;
	private final TokenRedirectProvider tokenRedirectProvider;

	/**
	 * 추가 정보 입력 페이지 진입
	 */
	@GetMapping("/additional-info")
	public String showAdditionalInfoPage(HttpServletRequest request, Model model) {
		// OAuth2 로그인 후 tempPrincipal 이 세션에 없으면 비정상 접근
		PrincipalDetails principalDetails =
			(PrincipalDetails)request.getSession().getAttribute("tempPrincipal");

		if (principalDetails == null) {
			model.addAttribute("error", "비정상 접근입니다.");
			return "error";
		}

		// 템플릿에 표시할 이메일(선택사항)
		model.addAttribute("email", Objects.requireNonNull(principalDetails.getName()));
		return "additional-info";
	}

	/**
	 * AJAX: 학번 유효성 체크
	 */
	@ResponseBody
	@GetMapping("/additional-info/check")
	public ResponseEntity<?> checkStudentNumber(@RequestParam("studentNumber") String studentNumber) {
		try {
			Student student = studentService.getStudentByStudentNumber(studentNumber);
			return ResponseEntity.ok().body(
				Map.of("name", student.getName(),
					"studentNumber", student.getStudentNumber())
			);
		} catch (StudentNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message", "존재하지 않는 학번입니다."));
		}
	}

	/**
	 * AJAX: Student – Provider 연결 최종 확정
	 */
	@ResponseBody
	@PostMapping("/additional-info/connect")
	public ResponseEntity<?> connectStudent(
		HttpServletRequest request,
		@RequestParam("studentNumber") String studentNumber
	) {
		// OAuth2 로그인 후 tempPrincipal 이 세션에 없으면 비정상 접근
		PrincipalDetails principalDetails =
			(PrincipalDetails)request.getSession().getAttribute("tempPrincipal");

		if (principalDetails == null) {
			return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(Map.of("message", "세션이 만료되었습니다. 다시 로그인 해주세요."));
		}

		// 한 번 사용 후 세션에서 제거 -> 중복 연결 방지
		request.getSession().removeAttribute("tempPrincipal");

		String email = principalDetails.getName();
		Student student = studentService.getStudentByStudentNumber(studentNumber);
		providerService.connectStudent(email, student);

		// Security Authentication 객체 구성해서 토큰 발행
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			principalDetails, null, principalDetails.getAuthorities()
		);
		String targetUrl = tokenRedirectProvider.generateRedirectUrl(request, authentication, email);

		return ResponseEntity.ok().body(
			Map.of("message", "연결 완료", "redirectUrl", targetUrl)
		);
	}
}
