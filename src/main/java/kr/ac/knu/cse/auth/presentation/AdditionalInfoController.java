package kr.ac.knu.cse.auth.presentation;

import java.io.IOException;
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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ac.knu.cse.auth.exception.InvalidRedirectUrlException;
import kr.ac.knu.cse.auth.exception.SessionExpiredException;
import kr.ac.knu.cse.provider.application.ProviderService;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.student.application.StudentService;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.exception.StudentNotFoundException;
import kr.ac.knu.cse.token.presentation.TokenProvisioner;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdditionalInfoController {

	private final StudentService studentService;
	private final ProviderService providerService;
	private final TokenProvisioner tokenProvisioner;

	@GetMapping("/additional-info")
	public String showAdditionalInfoPage(HttpServletRequest request, Model model) {
		PrincipalDetails principalDetails =
			(PrincipalDetails)request.getSession().getAttribute("tempPrincipal");

		if (principalDetails == null) {
			throw new SessionExpiredException();
		}

		model.addAttribute("email", Objects.requireNonNull(principalDetails.getName()));
		return "additional-info";
	}

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

	@ResponseBody
	@PostMapping("/additional-info/connect")
	public ResponseEntity<?> connectStudent(
		HttpServletRequest request,
		HttpServletResponse response,
		@RequestParam("studentNumber") String studentNumber
	) throws IOException {
		HttpSession session = request.getSession();
		PrincipalDetails principalDetails = (PrincipalDetails)session.getAttribute("tempPrincipal");

		if (principalDetails == null) {
			throw new SessionExpiredException();
		}

		String redirectUrl = (String)session.getAttribute("redirectUrl");
		if (redirectUrl == null || redirectUrl.isBlank()) {
			throw new InvalidRedirectUrlException();
		}

		session.removeAttribute("tempPrincipal");
		session.removeAttribute("redirectUrl");

		// 프로바이더 - 학생 연결
		String email = principalDetails.getName();
		Student student = studentService.getStudentByStudentNumber(studentNumber);
		providerService.connectStudent(email, student);

		// 새 인증 토큰 발급
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			principalDetails, null, principalDetails.getAuthorities()
		);
		tokenProvisioner.tokenIssue(authentication, response);

		// 정상 연결 후 프론트에서 redirect
		return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
	}
}
