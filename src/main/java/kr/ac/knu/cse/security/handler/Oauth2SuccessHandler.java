package kr.ac.knu.cse.security.handler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ac.knu.cse.auth.exception.InvalidRedirectUrlException;
import kr.ac.knu.cse.auth.exception.PrincipalDetailsNotFoundException;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.token.presentation.TokenProvisioner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final TokenProvisioner tokenProvisioner;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {

		if (!(authentication.getPrincipal() instanceof PrincipalDetails principalDetails)) {
			throw new PrincipalDetailsNotFoundException();
		}

		String email = principalDetails.getName();
		HttpSession session = request.getSession();
		String redirectUrl = (String)session.getAttribute("redirectUrl");

		if (redirectUrl == null || redirectUrl.isBlank()) {
			throw new InvalidRedirectUrlException();
		}

		// knu.ac.kr 이메일 도메인 체크
		if (!Objects.requireNonNull(email).endsWith("@knu.ac.kr")) {
			log.warn("허용되지 않은 이메일 도메인: {}", email);
			String encodedRedirect = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);
			getRedirectStrategy().sendRedirect(request, response, "/login/warning?redirectUrl=" + encodedRedirect);
			return;
		}

		// Student 엔티티 연결 여부에 따라 추가 정보 등록 필요
		if (principalDetails.provider().getStudent() == null) {
			session.setAttribute("tempPrincipal", principalDetails);
			getRedirectStrategy().sendRedirect(request, response, "/additional-info");
			return;
		}

		// 세션 삭제
		session.removeAttribute("redirectUrl");

		// 토큰 생성 및 쿠키 주입
		tokenProvisioner.tokenIssue(authentication, response);

		// 최종적으로 원래 redirectUrl로 이동
		response.sendRedirect(redirectUrl);
	}
}
