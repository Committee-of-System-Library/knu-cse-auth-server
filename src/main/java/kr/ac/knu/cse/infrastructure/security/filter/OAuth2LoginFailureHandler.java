package kr.ac.knu.cse.infrastructure.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.ac.knu.cse.global.exception.BusinessException;
import kr.ac.knu.cse.global.exception.auth.InvalidOAuthClientException;
import kr.ac.knu.cse.global.exception.auth.InvalidOidcUserException;
import kr.ac.knu.cse.global.exception.auth.OAuthAuthenticationFailedException;
import kr.ac.knu.cse.global.exception.auth.UnauthorizedOAuthClientException;
import kr.ac.knu.cse.global.exception.common.InternalServerErrorException;
import kr.ac.knu.cse.infrastructure.security.support.FilterBusinessExceptionWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final static String INVALID_GRANT = "invalid_grant";
    private final static String ACCESS_DENIED = "access_denied";
    private final static String DISABLED_ACCOUNT = "Account is disabled";
    private final static String INVALID_CLIENT = "invalid_client";
    private final static String UNAUTHORIZED_CLIENT = "unauthorized_client";

    private final FilterBusinessExceptionWriter filterBusinessExceptionWriter;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        filterBusinessExceptionWriter.write(response, translate(exception));
    }

    private BusinessException translate(AuthenticationException ex) {
        if (ex instanceof OAuth2AuthenticationException oauthEx) {
            return handleOAuth2Exception(oauthEx);
        }

        return new InternalServerErrorException();
    }

    private BusinessException handleOAuth2Exception(
            OAuth2AuthenticationException ex
    ) {
        OAuth2Error error = ex.getError();
        String errorCode = error.getErrorCode();
        String description = error.getDescription();

        log.warn("OAuth2 failure. errorCode={}, description={}",
                errorCode, description);

        if (INVALID_GRANT.equals(errorCode) ) {
            return new OAuthAuthenticationFailedException();
        }

        if (ACCESS_DENIED.equals(errorCode)) {
            return new InvalidOidcUserException();
        }

        if (INVALID_CLIENT.equals(errorCode)) {
            return new InvalidOAuthClientException();
        }
        if (UNAUTHORIZED_CLIENT.equals(errorCode)) {
            return new UnauthorizedOAuthClientException();
        }

        return new InternalServerErrorException();
    }
}
