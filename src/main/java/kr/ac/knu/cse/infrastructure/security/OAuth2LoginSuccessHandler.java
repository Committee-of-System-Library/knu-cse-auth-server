package kr.ac.knu.cse.infrastructure.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import kr.ac.knu.cse.application.auth.OAuthLoginService;
import kr.ac.knu.cse.application.auth.OidcUserInfoMapper;
import kr.ac.knu.cse.application.auth.dto.OAuthLoginResult;
import kr.ac.knu.cse.infrastructure.keycloak.KeycloakAdminClient;
import kr.ac.knu.cse.presentation.auth.LoginController;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "ACCESS_TOKEN";
    private static final int COOKIE_MAX_AGE = 60 * 60;

    private final OidcUserInfoMapper oidcUserInfoMapper;
    private final OAuthLoginService oAuthLoginService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final KeycloakAdminClient keycloakAdminClient;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        validate(authentication);

        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;
        OidcUser oidcUser =
                (OidcUser) authentication.getPrincipal();

        OAuthLoginResult result = oAuthLoginService.login(
                oidcUserInfoMapper.map(oidcUser)
        );

        keycloakAdminClient.upsertStudentIdAttribute(oidcUser.getSubject(), result.studentId());    // save studentId in Keycloak user attribute
        keycloakAdminClient.ensureRealmRole(oidcUser.getSubject(), "ROLE_USER");    // assign ROLE_USER to role in keycloak

        OAuth2AccessToken accessToken = extractAccessToken(oauthToken);
        validateAccessToken(accessToken);

        setAccessTokenCookie(response, accessToken.getTokenValue());

        doRedirect(request, response);
    }

    public void validate( Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            throw new IllegalStateException("Unexpected authentication type: " + authentication);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof OidcUser)) {
            throw new IllegalStateException("Principal is not OidcUser");
        }
    }

    private OAuth2AccessToken extractAccessToken(OAuth2AuthenticationToken oauthToken) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );
        validateClient(client);
        return client.getAccessToken();
    }

    private void validateClient(OAuth2AuthorizedClient client) {
        if (client == null) {
            throw new IllegalStateException("OAuth2AuthorizedClient not found");
        }
    }

    private void validateAccessToken(OAuth2AccessToken accessToken) {
        if (accessToken == null) {
            throw new IllegalStateException("Access token is null");
        }
    }

    private void setAccessTokenCookie(
            HttpServletResponse response,
            String tokenValue) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, tokenValue);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);

        response.addCookie(cookie);
    }

    private void doRedirect(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendError(400, "Missing session");
            return;
        }

        String redirectUri = (String) session.getAttribute(LoginController.SESSION_REDIRECT_URI);
        String state = (String) session.getAttribute(LoginController.SESSION_STATE);

        if (redirectUri == null || state == null) {
            response.sendError(400, "Missing redirect_uri/state in session");
            return;
        }

        response.sendRedirect(redirectUri + "?state=" + state);
    }
}
