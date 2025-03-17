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
import kr.ac.knu.cse.token.exception.TokenExpiredException;
import kr.ac.knu.cse.token.exception.TokenInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationFilter extends GenericFilterBean {

	private final JwtTokenService jwtTokenService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest servletRequest = (HttpServletRequest)request;
		HttpServletResponse servletResponse = (HttpServletResponse)response;

		if ("OPTIONS".equals(servletRequest.getMethod())) {
			chain.doFilter(request, response);
			return;
		}

		String tokenValue = jwtTokenService.resolveToken(servletRequest);
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
			log.error("[AuthorizationFilter] 토큰 만료", e);
			servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
			throw new TokenExpiredException();
		} catch (JwtException e) {
			log.error("[AuthorizationFilter] 토큰 파싱 오류", e);
			servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
			throw new TokenInvalidException();
		} catch (Exception e) {
			log.error("[AuthorizationFilter] 토큰 검사 중 예외 발생", e);
			servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token error");
			throw new TokenInvalidException();
		}

		chain.doFilter(request, response);
	}
}
