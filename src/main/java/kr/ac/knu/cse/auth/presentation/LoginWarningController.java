package kr.ac.knu.cse.auth.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginWarningController {

	@GetMapping("/login/warning")
	public String showLoginWarningPage(
		@RequestParam(name = "redirectUrl", required = false) String redirectUrl,
		Model model
	) {
		if (redirectUrl == null || redirectUrl.isBlank()) {
			redirectUrl = "/";
		}
		model.addAttribute("redirectUrl", redirectUrl);
		return "login-warning";
	}
}
