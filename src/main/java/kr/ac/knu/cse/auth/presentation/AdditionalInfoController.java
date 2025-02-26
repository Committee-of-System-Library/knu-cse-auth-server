package kr.ac.knu.cse.auth.presentation;

import java.util.Objects;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.knu.cse.auth.applicaiton.TokenRedirectProvider;
import kr.ac.knu.cse.provider.application.ProviderService;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.student.application.StudentService;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.token.application.JwtTokenService;
import kr.ac.knu.cse.token.application.RefreshTokenService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdditionalInfoController {

	private final StudentService studentService;
	private final ProviderService providerService;
	private final JwtTokenService jwtTokenService;
	private final RefreshTokenService refreshTokenService;
	private final TokenRedirectProvider tokenRedirectProvider;

	@GetMapping("/additional-info")
	public String showAdditionalInfoPage(HttpServletRequest request, Model model) {
		// 비정상 접근 검증
		PrincipalDetails principalDetails =
			(PrincipalDetails) request.getSession().getAttribute("tempPrincipal");

		if (principalDetails == null) {
			model.addAttribute("error", "비정상 접근입니다.");
			return "error";
		}

		// 추가정보 페이지에서 보여줄 email
		model.addAttribute("email", Objects.requireNonNull(principalDetails.getName()));
		return "additional-info";
	}

	@PostMapping("/additional-info")
	public String processAdditionalInfo(HttpServletRequest request,
		@RequestParam("studentNumber") String studentNumber
	) {
		// 비정상 접근 검증
		PrincipalDetails principalDetails =
			(PrincipalDetails) request.getSession().getAttribute("tempPrincipal");

		if (principalDetails == null)
			throw new IllegalStateException("비정상 접근입니다. OAuth2 로그인 세션이 없습니다.");
		else request.getSession().removeAttribute("tempPrincipal");

		// Provider 와 Student 연결
		String email = principalDetails.getName();
		Student student = studentService.getStudentByStudentNumber(studentNumber);
		providerService.connectStudent(email, student);

		// Spring Security Authentication 객체 구성
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			principalDetails, null, principalDetails.getAuthorities()
		);

		// 공통 토큰 생성 및 리다이렉트 URL 구성
		String targetUrl = tokenRedirectProvider.generateRedirectUrl(request, authentication, email);
		return "redirect:" + targetUrl;
	}
}
