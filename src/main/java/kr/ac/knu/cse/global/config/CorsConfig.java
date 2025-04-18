package kr.ac.knu.cse.global.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(
			"https://dev.knucse.site",
			"http://localhost:5173"
		));
		configuration.setAllowedMethods(Arrays.asList(
			HttpMethod.GET.name(),
			HttpMethod.POST.name(),
			HttpMethod.PATCH.name(),
			HttpMethod.PUT.name(),
			HttpMethod.DELETE.name()
		));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
