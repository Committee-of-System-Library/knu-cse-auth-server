package kr.ac.knu.cse.auth.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import kr.ac.knu.cse.global.properties.AppProperties;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
	private final AppProperties appProperties;

	@GetMapping("/login")
	public String showLoginPage(String redirectUrl, HttpSession session, Model model) {

		if (redirectUrl == null || redirectUrl.isBlank()) {
			redirectUrl = "/";
		}

		if (!appProperties.getAllowedRedirects().contains(redirectUrl)) {
			throw new IllegalArgumentException("허용되지 않은 redirectUrl 입니다: " + redirectUrl);
		}

		session.setAttribute("redirectUrl", redirectUrl);
		
		return "login";
	}
}
