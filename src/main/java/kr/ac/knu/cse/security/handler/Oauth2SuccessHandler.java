package kr.ac.knu.cse.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ac.knu.cse.auth.application.AuthorizationCodeService;
import kr.ac.knu.cse.auth.application.TempAuthStorage;
import kr.ac.knu.cse.auth.exception.InvalidRedirectUrlException;
import kr.ac.knu.cse.auth.exception.PrincipalDetailsNotFoundException;
import kr.ac.knu.cse.security.details.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AuthorizationCodeService authorizationCodeService;
    private final TempAuthStorage tempAuthStorage;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        log.info("OAuth2 인증 성공 핸들러 시작");

        if (!(authentication.getPrincipal() instanceof PrincipalDetails principalDetails)) {
            log.error("PrincipalDetails 타입이 아닙니다. authentication.getPrincipal(): {}", authentication.getPrincipal().getClass());
            throw new PrincipalDetailsNotFoundException();
        }

        String email = principalDetails.getName();
        HttpSession session = request.getSession();
        String redirectUrl = (String) session.getAttribute("redirectUrl");
        log.debug("인증 성공 - 사용자: {}, Redirect URL: {}", email, redirectUrl);

        if (redirectUrl == null || redirectUrl.isBlank()) {
            log.error("Redirect URL이 세션에 없습니다.");
            throw new InvalidRedirectUrlException();
        }

        // knu.ac.kr 이메일 도메인 검사
        if (!Objects.requireNonNull(email).endsWith("@knu.ac.kr")) {
            log.warn("허용되지 않은 이메일 도메인: {}. 로그인 경고 페이지로 리다이렉트합니다.", email);
            String encodedRedirect = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);
            getRedirectStrategy().sendRedirect(request, response, "/login/warning?redirectUrl=" + encodedRedirect);
            return;
        }

        // 최초 로그인 여부 확인
        if (principalDetails.provider().getStudent() == null) {
            log.info("사용자({})가 아직 학생 정보와 연결되지 않았습니다. 추가 정보 입력 페이지로 리다이렉트합니다.", email);

            // 임시 토큰에 PrincipalDetails 저장
            String tempToken = tempAuthStorage.storePrincipalDetails(principalDetails);
            log.info("임시 인증 토큰 생성 (PrincipalDetails만): {}", tempToken);

            // 프론트엔드 추가 정보 페이지로 리다이렉트
            String encodedRedirectUrl = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);
            String frontendAdditionalInfoUrl = frontendBaseUrl + "/additional-info?token=" + tempToken + "&redirectUrl=" + encodedRedirectUrl;
            log.info("프론트엔드 추가 정보 페이지로 리다이렉트: {}", frontendAdditionalInfoUrl);
            getRedirectStrategy().sendRedirect(request, response, frontendAdditionalInfoUrl);
            return;
        }

        log.info("사용자({})가 학생 정보와 연결되어 있습니다. Authorization Code를 발급합니다.", email);

        // Authorization Code 생성
        String authorizationCode = authorizationCodeService.generateCode(email, redirectUrl);
        log.info("사용자({})에게 Authorization Code 발급 완료", email);

        // 최종 redirectUrl로 code와 함께 이동
        String finalRedirectUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("code", authorizationCode)
                .build().toUriString();

        log.info("최종 목적지인 {}로 리다이렉트합니다.", finalRedirectUrl);
        getRedirectStrategy().sendRedirect(request, response, finalRedirectUrl);
    }
}
