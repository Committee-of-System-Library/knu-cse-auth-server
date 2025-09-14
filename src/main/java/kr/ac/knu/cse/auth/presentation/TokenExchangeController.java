package kr.ac.knu.cse.auth.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.ac.knu.cse.auth.application.AuthorizationCodeService;
import kr.ac.knu.cse.auth.domain.AuthorizationCode;
import kr.ac.knu.cse.auth.presentation.dto.TokenExchangeRequest;
import kr.ac.knu.cse.auth.presentation.dto.TokenExchangeResponse;
import kr.ac.knu.cse.client.domain.AuthClient;
import kr.ac.knu.cse.client.exception.AuthClientNotFoundException;
import kr.ac.knu.cse.client.exception.UnauthorizedServiceAccessException;
import kr.ac.knu.cse.client.persistence.AuthClientRepository;
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
    private final AuthClientRepository authClientRepository;
    private final JwtProperties jwtProperties;

    @PostMapping
    public ResponseEntity<ApiSuccessResult<TokenExchangeResponse>> exchangeToken(
            @Valid @RequestBody TokenExchangeRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("토큰 교환 요청 - Code: {}, redirectUrl: {}", request.getCode(), request.getRedirectUrl());

        AuthorizationCode authorizationCode = authorizationCodeService
                .validateAndConsumeCode(request.getCode(), request.getRedirectUrl());

        AuthClient client = authClientRepository.findById(authorizationCode.getClientId())
                .orElseThrow(() -> {
                    log.error("클라이언트를 찾을 수 없습니다: ClientId={}", authorizationCode.getClientId());
                    return new AuthClientNotFoundException();
                });

        String origin = httpRequest.getHeader("Origin");

        log.info("교차 검증 - Origin: {}, RedirectUrl: {}, ClientId: {}",
                origin, authorizationCode.getRedirectUrl(), client.getClientId());

        if (!client.isValidRedirectRequest(authorizationCode.getRedirectUrl(), origin)) {
            log.warn("redirectUrl과 Origin이 일치하지 않음 - Origin: {}, RedirectUrl: {}, ClientId: {}",
                    origin, authorizationCode.getRedirectUrl(), client.getClientId());
            throw new UnauthorizedServiceAccessException();
        }

        Provider provider = providerRepository.findByEmail(authorizationCode.getEmail())
                .orElseThrow(() -> {
                    log.error("Provider를 찾을 수 없습니다: {}", authorizationCode.getEmail());
                    return new ProviderNotFoundException();
                });

        PrincipalDetails principalDetails = PrincipalDetails.builder()
                .provider(provider)
                .student(provider.getStudent())
                .attributes(null)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principalDetails, "", principalDetails.getAuthorities());

        Token token = jwtTokenService.generateToken(authentication, client.getClientId());

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
