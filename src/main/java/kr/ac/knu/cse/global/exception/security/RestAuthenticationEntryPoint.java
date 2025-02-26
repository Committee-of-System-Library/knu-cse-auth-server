package kr.ac.knu.cse.global.exception.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.knu.cse.global.api.ApiErrorResult;
import kr.ac.knu.cse.global.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증이 필요한 자원에 미인증(anonymous) 상태로 접근할 경우
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request,
		HttpServletResponse response,
		org.springframework.security.core.AuthenticationException authException)
		throws IOException, ServletException {

		log.error("[AuthEntryPoint] Authentication error: {}", authException.getMessage());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");

		ApiErrorResult errorBody = ApiResponse.error(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
		objectMapper.writeValue(response.getWriter(), errorBody);
	}
}
