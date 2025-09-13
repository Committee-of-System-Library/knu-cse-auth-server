package kr.ac.knu.cse.token.presentation;

import kr.ac.knu.cse.token.application.JwtTokenService;
import kr.ac.knu.cse.token.domain.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenProvisioner {
    private final JwtTokenService jwtTokenService;

    public String provisionToken(Authentication authentication) {
        Token accessToken = jwtTokenService.generateToken(authentication);
        return accessToken.getValue();
    }
}
