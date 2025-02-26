package kr.ac.knu.cse.security.annotation;


import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.annotation.Nonnull;
import kr.ac.knu.cse.provider.excpetion.NotConnectedStudentException;
import kr.ac.knu.cse.provider.excpetion.ProviderNotFoundException;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggedInProviderArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(@Nonnull MethodParameter parameter) {
		return parameter.hasParameterAnnotation(LoggedInProvider.class)
			&& parameter.getParameterType().equals(PrincipalDetails.class);
	}

	@Override
	public PrincipalDetails resolveArgument(
		@Nonnull MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		@Nonnull NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory
	) {
		Authentication authentication = SecurityContextHolder
			.getContext()
			.getAuthentication();

		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		if (principalDetails.provider() == null) throw new ProviderNotFoundException();
		if (principalDetails.student() == null) throw new NotConnectedStudentException();

		return principalDetails;
	}
}
