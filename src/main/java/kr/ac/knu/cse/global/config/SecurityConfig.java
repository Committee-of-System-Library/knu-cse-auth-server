package kr.ac.knu.cse.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import kr.ac.knu.cse.global.exception.security.RestAccessDeniedHandler;
import kr.ac.knu.cse.global.exception.security.RestAuthenticationEntryPoint;
import kr.ac.knu.cse.security.details.PrincipalDetailsOauthService;
import kr.ac.knu.cse.security.filter.AuthorizationFilter;
import kr.ac.knu.cse.security.handler.Oauth2SuccessHandler;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final PrincipalDetailsOauthService principalDetailsOauthService;
	private final Oauth2SuccessHandler oauth2SuccessHandler;
	private final AuthorizationFilter authorizationFilter;
	private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	private final RestAccessDeniedHandler restAccessDeniedHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(
		HttpSecurity httpSecurity
	) throws Exception {
		httpSecurity
			.cors(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.formLogin(AbstractHttpConfigurer::disable);

		httpSecurity
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(restAuthenticationEntryPoint)
				.accessDeniedHandler(restAccessDeniedHandler));

		httpSecurity
			.authorizeHttpRequests(request -> request
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
				.anyRequest().authenticated())
			.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
			.httpBasic(AbstractHttpConfigurer::disable);

		httpSecurity
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorization -> authorization
					.baseUri("/oauth2/authorize")) // OAuth2 인증 시작 URL 설정
				.redirectionEndpoint(redirection -> redirection
					.baseUri("/oauth2/callback/*")) // OAuth2 콜백 URL 설정
				.userInfoEndpoint(userInfo -> userInfo
					.userService(principalDetailsOauthService))
				.successHandler(oauth2SuccessHandler)); // 인증 성공 핸들러

		return httpSecurity.build();
	}
}
