package kr.ac.knu.cse.security.handler;

import java.io.IOException;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import kr.ac.knu.cse.token.application.JwtTokenService;
import kr.ac.knu.cse.token.application.RefreshTokenService;
import kr.ac.knu.cse.token.domain.RefreshToken;
import kr.ac.knu.cse.token.domain.Token;
import kr.ac.knu.cse.token.domain.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException {

        if (!(authentication.getPrincipal() instanceof PrincipalDetails principalDetails)) {
            log.error("Authentication Principal is not PrincipalDetails");
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Authentication error");
            return;
        }

        String email = principalDetails.getName(); // Provider.email
        log.info("OAuth2 login 성공, email: {}", email);

        // @knu.ac.kr 계정인지 확인
        if (!Objects.requireNonNull(email).endsWith("@knu.ac.kr")) {
            log.error("허용되지 않은 이메일 도메인: {}", email);
            response.sendError(HttpStatus.FORBIDDEN.value(), "@knu.ac.kr 계정만 사용 가능합니다.");
            return;
        }

        // [추가] Student 연결 여부 확인
        if (principalDetails.provider().getStudent() == null) {
            // 아직 학생 정보가 없으므로 → 학생번호 입력받는 페이지(혹은 URL)로 이동시킴
            // (예: /additional-info?email=xxx@knu.ac.kr)
            String redirectUrl = "/additional-info?email=" + email;
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            return;
        }

        // 이미 Student와 연결된 경우 → JWT 발급 로직 그대로 진행
        Token accessToken = jwtTokenService.generateToken(authentication, TokenType.ACCESS_TOKEN);
        Token refreshToken = jwtTokenService.generateToken(authentication, TokenType.REFRESH_TOKEN);

        refreshTokenService.updateRefreshToken(
            RefreshToken.builder()
                .email(email)
                .refreshToken(refreshToken.value())
                .build()
        );

        // 로그인 페이지에서 세션에 저장한 redirectUrl 꺼냄
        String redirectUrl = (String) request.getSession().getAttribute("redirectUrl");
        if (redirectUrl == null || redirectUrl.isBlank()) {
            redirectUrl = "/";
        }

        // 토큰 쿼리 파라미터 붙이기
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
            .queryParam("access_token", accessToken.value())
            .queryParam("refresh_token", refreshToken.value())
            .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
