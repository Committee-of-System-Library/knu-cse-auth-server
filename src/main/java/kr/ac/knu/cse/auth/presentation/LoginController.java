package kr.ac.knu.cse.auth.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.ac.knu.cse.auth.exception.InvalidRedirectUrlException;
import kr.ac.knu.cse.global.properties.AppProperties;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
	private final AppProperties appProperties;

	@GetMapping("/login")
	public String showLoginPage(HttpSession session) {
		session.removeAttribute("redirectUrl");
		return "login";
	}

	@GetMapping("/oauth2/login/google")
	public String googleLogin(
		@RequestParam(required = false) String redirectUrl,
		HttpSession session
	) {
		if (redirectUrl == null || redirectUrl.isBlank()) {
			redirectUrl = "https://example.com";
		}

		if (!appProperties.getAllowedRedirects().contains(redirectUrl)) {
			throw new InvalidRedirectUrlException();
		}

		session.setAttribute("redirectUrl", redirectUrl);
		return "redirect:/oauth2/authorize/google";
	}
}
