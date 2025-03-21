package kr.ac.knu.cse.security.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.knu.cse.token.application.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationFilter extends GenericFilterBean {

	private final JwtTokenService jwtTokenService;

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

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest servletRequest = (HttpServletRequest)request;
		HttpServletResponse servletResponse = (HttpServletResponse)response;

		String method = servletRequest.getMethod();
		if ("OPTIONS".equalsIgnoreCase(method)) {
			chain.doFilter(request, response);
			return;
		}

		String contextPath = servletRequest.getContextPath();
		String path = servletRequest.getRequestURI().substring(contextPath.length());
		for (String prefix : EXCLUDE_URL_PREFIXES) {
			if (path.startsWith(prefix)) {
				chain.doFilter(request, response);
				return;
			}
		}

		String tokenValue = jwtTokenService.resolveToken(servletRequest, "access_token");
		if (tokenValue == null) {
			chain.doFilter(request, response);
			return;
		}

		try {
			Jws<Claims> parsedToken = jwtTokenService.extractClaims(tokenValue);
			String email = jwtTokenService.extractEmail(parsedToken);
			Authentication authentication = jwtTokenService.getAuthentication(email);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (ExpiredJwtException e) {
			log.error("[AuthorizationFilter] 토큰 만료: {}", e.getMessage());
			servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
			return;
		} catch (JwtException e) {
			log.error("[AuthorizationFilter] 토큰 파싱 오류: {}", e.getMessage());
			servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
			return;
		} catch (Exception e) {
			log.error("[AuthorizationFilter] 토큰 검사 중 예외 발생: {}", e.getMessage());
			servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token error");
			return;
		}

		chain.doFilter(request, response);
	}
}
