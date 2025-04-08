package kr.ac.knu.cse.global.exception.security;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.knu.cse.global.api.ApiErrorResult;
import kr.ac.knu.cse.global.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException
	) throws IOException {
		String accept = request.getHeader("Accept");
		String requestURI = request.getRequestURI();

		boolean isHtmlRequest = (accept != null && accept.contains("text/html"))
			|| requestURI.startsWith("/auth/manage");

		if (isHtmlRequest) {
			String fullURL = request.getRequestURL().toString();

			String encodedUrl = URLEncoder.encode(fullURL, java.nio.charset.StandardCharsets.UTF_8);
			String redirectPath = "/auth/login?redirectUrl=" + encodedUrl;

			response.sendRedirect(redirectPath);

		} else {
			log.error("[AuthEntryPoint] Authentication error: {}", authException.getMessage());

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			ApiErrorResult errorBody = ApiResponse.error(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
			objectMapper.writeValue(response.getWriter(), errorBody);
		}
	}
}
