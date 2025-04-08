package kr.ac.knu.cse.qr.presentation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QrAuthController {

	@GetMapping("/qr-auth")
	@PreAuthorize("hasAnyRole('EXECUTIVE','FINANCE','ADMIN')")
	public String showQrAuthPage() {
		return "qr-auth";
	}
}
