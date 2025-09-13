package kr.ac.knu.cse.global.config;

import kr.ac.knu.cse.security.details.PrincipalDetailsOauthService;
import kr.ac.knu.cse.security.filter.AuthorizationFilter;
import kr.ac.knu.cse.security.handler.Oauth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final PrincipalDetailsOauthService principalDetailsOauthService;
	private final Oauth2SuccessHandler oauth2SuccessHandler;
	private final AuthorizationFilter authorizationFilter;
	private final CorsConfigurationSource corsConfigurationSource;

	@Bean
	public RoleHierarchy roleHierarchy() {
		return RoleHierarchyImpl.fromHierarchy(
			"""
				ROLE_ADMIN > ROLE_EXECUTIVE\s
				ROLE_ADMIN > ROLE_FINANCE\s
				ROLE_EXECUTIVE > ROLE_STUDENT\s
				ROLE_FINANCE > ROLE_STUDENT"""
		);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(
		HttpSecurity httpSecurity
	) throws Exception {
		httpSecurity
			.cors(cors -> cors.configurationSource(corsConfigurationSource))
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.formLogin(AbstractHttpConfigurer::disable);

		httpSecurity
			.authorizeHttpRequests(request -> request
				.requestMatchers(
					new AntPathRequestMatcher("/oauth2/**"),
					new AntPathRequestMatcher("/additional-info/**"),
					new AntPathRequestMatcher("/token"),
					new AntPathRequestMatcher("/login"),
					new AntPathRequestMatcher("/h2-console/**")
				).permitAll()
				.anyRequest().authenticated())
			.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

		httpSecurity
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorization -> authorization
					.baseUri("/oauth2/authorize")) // OAuth2 인증 시작 URL 설정
				.redirectionEndpoint(redirection -> redirection
					.baseUri("/oauth2/callback/*")) // OAuth2 콜백 URL 설정
				.userInfoEndpoint(userInfo -> userInfo
					.userService(principalDetailsOauthService))
				.successHandler(oauth2SuccessHandler)); // 인증 성공 핸들러

		// X-Frame-Options 설정 (for H2 Console)
		httpSecurity.headers(
			headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

		return httpSecurity.build();
	}
}
