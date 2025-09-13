package kr.ac.knu.cse.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.knu.cse.global.exception.dto.ErrorResponse;
import kr.ac.knu.cse.token.application.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

	private static final String[] EXCLUDE_URL_PREFIXES = {
		"/css",
		"/js",
		"/images",
		"/h2-console",
		"/error",
		"/favicon.ico",
		"/login",
		"/oauth2/login",
		"/oauth2/authorize",
		"/additional-info"
	};
	private final JwtTokenService jwtTokenService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String method = request.getMethod();
		if ("OPTIONS".equalsIgnoreCase(method)) {
			log.debug("OPTIONS 요청은 통과시킵니다.");
			filterChain.doFilter(request, response);
			return;
		}

		String contextPath = request.getContextPath();
		String path = request.getRequestURI().substring(contextPath.length());
		log.debug("요청 수신: {} {}", method, path);

		for (String prefix : EXCLUDE_URL_PREFIXES) {
			if (path.startsWith(prefix)) {
				log.debug("제외 URL({}) 요청이므로 필터를 통과합니다.", path);
				filterChain.doFilter(request, response);
				return;
			}
		}

		String tokenValue = jwtTokenService.resolveToken(request);
		if (tokenValue == null) {
			log.debug("Access Token이 없습니다. 다음 필터로 넘어갑니다.");
			filterChain.doFilter(request, response);
			return;
		}

		try {
			log.debug("Access Token 발견. 토큰 검증을 시작합니다.");
			Jws<Claims> parsedToken = jwtTokenService.extractClaims(tokenValue);
			String email = jwtTokenService.extractEmail(parsedToken);
			Authentication authentication = jwtTokenService.getAuthentication(email);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.info("사용자({})에 대한 인증 컨텍스트를 설정했습니다.", email);
		} catch (ExpiredJwtException e) {
			log.warn("[AuthorizationFilter] Access Token이 만료되었습니다. URI: {}, 토큰: {}", path, tokenValue);
			SecurityContextHolder.clearContext();
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "EXPIRED_TOKEN", "토큰이 만료되었습니다.");
			return;
		} catch (JwtException e) {
			log.error("[AuthorizationFilter] Access Token 파싱 또는 검증에 실패했습니다. URI: {}, 토큰: {}, 오류: {}", path, tokenValue, e.getMessage());
			SecurityContextHolder.clearContext();
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다.");
			return;
		} catch (Exception e) {
			log.error("[AuthorizationFilter] 토큰 검사 중 예상치 못한 예외가 발생했습니다. URI: {}, 오류: {}", path, e.getMessage(), e);
			SecurityContextHolder.clearContext();
			sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "TOKEN_VALIDATION_ERROR", "토큰 검증 중 오류가 발생했습니다.");
			return;
		}

		filterChain.doFilter(request, response);
	}
	
	private void sendErrorResponse(HttpServletResponse response, int status, String error, String message) 
		throws IOException {
		ErrorResponse errorResponse = ErrorResponse.of(error, message, status);
		
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
