package kr.ac.knu.cse.security.handler;

import java.io.IOException;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.knu.cse.auth.applicaiton.TokenRedirectProvider;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenRedirectProvider tokenRedirectProvider;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        if (!(authentication.getPrincipal() instanceof PrincipalDetails principalDetails)) {
            log.error("Authentication Principal is not PrincipalDetails");
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Authentication error");
            return;
        }

        String email = principalDetails.getName();
        log.info("OAuth2 login 성공, email: {}", email);

        // knu.ac.kr 이메일 도메인 체크
        if (!Objects.requireNonNull(email).endsWith("@knu.ac.kr")) {
            log.error("허용되지 않은 이메일 도메인: {}", email);
            response.sendError(HttpStatus.FORBIDDEN.value(), "@knu.ac.kr 계정만 사용 가능합니다.");
            return;
        }

        // Student 엔티티 연결 여부에 따라 추가 정보 등록 필요
        if (principalDetails.provider().getStudent() == null) {
            request.getSession().setAttribute("tempPrincipal", principalDetails);
            getRedirectStrategy().sendRedirect(request, response, "/additional-info");
            return;
        }

        // 공통 토큰 생성 및 리다이렉트 URL 구성
        String targetUrl = tokenRedirectProvider.generateRedirectUrl(request, authentication, email);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
