package kr.ac.knu.cse.security.details;

import kr.ac.knu.cse.provider.application.ProviderService;
import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.security.dto.Oauth2ResponseDto;
import kr.ac.knu.cse.security.dto.Oauth2ResponseMatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalDetailsOauthService extends DefaultOAuth2UserService {
	private final ProviderService providerService;
	private final Oauth2ResponseMatcher oauth2ResponseMatcher;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("OAuth2 사용자 정보 로드를 시작합니다.");
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		log.debug("Registration ID: {}", registrationId);
		log.debug("OAuth2 User Attributes: {}", oAuth2User.getAttributes());


		Oauth2ResponseDto oauth2Response = oauth2ResponseMatcher.matcher(registrationId, oAuth2User);
		log.debug("매칭된 OAuth2 응답: {}", oauth2Response);

		Provider provider = providerService.loadOrSaveProvider(oauth2Response);
		log.info("Provider 정보 로드/저장 완료: {}", provider.getEmail());

		PrincipalDetails principalDetails = PrincipalDetails.builder()
			.student(provider.getStudent())
			.provider(provider)
			.attributes(oAuth2User.getAttributes())
			.build();

		log.info("PrincipalDetails 생성 완료: {}", principalDetails.getName());
		return principalDetails;
	}
}
