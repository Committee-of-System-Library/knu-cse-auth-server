package kr.ac.knu.cse.auth.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ac.knu.cse.client.persistence.AuthClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

	private final AuthClientRepository authClientRepository;

	@GetMapping("/login")
	public void login(
		@RequestParam("redirectUrl") String redirectUrl,
		HttpServletRequest request,
		HttpServletResponse response
	) throws IOException {
		log.info("로그인 요청 - Redirect URL: {}", redirectUrl);

		// redirect_url 유효성 검증 (DB 기반)
		if (authClientRepository.findByAllowedRedirectUrl(redirectUrl).isEmpty()) {
			log.warn("허용되지 않은 Redirect URL: {}", redirectUrl);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid redirect URL");
			return;
		}

		// 세션에 redirect URL 저장
		HttpSession session = request.getSession();
		session.setAttribute("redirectUrl", redirectUrl);
		log.debug("세션에 Redirect URL 저장: {}", redirectUrl);

		// Spring Security의 OAuth2 인증 시작 URL로 리다이렉트
		String oauth2AuthUrl = "/oauth2/authorize/google";
		log.info("Spring Security OAuth2 인증 URL로 리다이렉트: {}", oauth2AuthUrl);
		response.sendRedirect(oauth2AuthUrl);
	}

}
