package kr.ac.knu.cse.infrastructure.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.ac.knu.cse.infrastructure.security.support.CookieCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class OAuth2LogoutSuccessHandler implements LogoutSuccessHandler {

    private final CookieCreator cookieCreator;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        ResponseCookie deleteCookie = cookieCreator.create();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}

