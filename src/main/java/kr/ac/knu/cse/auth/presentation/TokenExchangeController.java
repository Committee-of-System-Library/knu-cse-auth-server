package kr.ac.knu.cse.auth.presentation;

import jakarta.validation.Valid;
import kr.ac.knu.cse.auth.application.AuthorizationCodeService;
import kr.ac.knu.cse.auth.domain.AuthorizationCode;
import kr.ac.knu.cse.auth.presentation.dto.TokenExchangeRequest;
import kr.ac.knu.cse.auth.presentation.dto.TokenExchangeResponse;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.global.properties.JwtProperties;
import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.provider.exception.ProviderNotFoundException;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.token.application.JwtTokenService;
import kr.ac.knu.cse.token.domain.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
@Slf4j
public class TokenExchangeController {
	
	private final AuthorizationCodeService authorizationCodeService;
	private final JwtTokenService jwtTokenService;
	private final ProviderRepository providerRepository;
	private final JwtProperties jwtProperties;
	
	@PostMapping
	public ResponseEntity<ApiSuccessResult<TokenExchangeResponse>> exchangeToken(
		@Valid @RequestBody TokenExchangeRequest request
	) {
		log.info("토큰 교환 요청 - Code: {}, redirectUrl: {}", request.getCode(), request.getRedirectUrl());
		
		// Authorization Code 검증 및 소비
		AuthorizationCode authorizationCode = authorizationCodeService
			.validateAndConsumeCode(request.getCode(), request.getRedirectUrl());
		
		// 사용자 정보 조회
		Provider provider = providerRepository.findByEmail(authorizationCode.getEmail())
			.orElseThrow(() -> {
				log.error("Provider를 찾을 수 없습니다: {}", authorizationCode.getEmail());
				return new ProviderNotFoundException();
			});
		
		// Authentication 객체 생성
		PrincipalDetails principalDetails = PrincipalDetails.builder()
			.provider(provider)
			.student(provider.getStudent())
			.attributes(null)
			.build();
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			principalDetails, "", principalDetails.getAuthorities());
		
		// JWT 토큰 생성
		Token token = jwtTokenService.generateToken(authentication);
		
		log.info("토큰 교환 완료 - Email: {}, TokenType: {}", 
			authorizationCode.getEmail(), token.getType());
		
		TokenExchangeResponse response = new TokenExchangeResponse(
			token.getValue(),
			token.getType(),
			jwtProperties.getExpiration()
		);
		
		return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, response));
	}
}
