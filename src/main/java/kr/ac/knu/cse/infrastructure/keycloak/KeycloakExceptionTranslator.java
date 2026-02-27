package kr.ac.knu.cse.infrastructure.keycloak;

import kr.ac.knu.cse.global.exception.BusinessException;
import kr.ac.knu.cse.global.exception.keycloak.KeycloakAuthFailedException;
import kr.ac.knu.cse.global.exception.keycloak.KeycloakForbiddenException;
import kr.ac.knu.cse.global.exception.keycloak.KeycloakUnavailableException;
import kr.ac.knu.cse.global.exception.keycloak.KeycloakUserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class KeycloakExceptionTranslator {

    public BusinessException translate(Exception e) {
        if (e instanceof HttpClientErrorException httpEx) {
            return translateHttpClientError(httpEx);
        }

        return new KeycloakUnavailableException();
    }

    private BusinessException translateHttpClientError(
            HttpClientErrorException e
    ) {
        HttpStatusCode status = e.getStatusCode();

        if (status == HttpStatus.UNAUTHORIZED) {
            return new KeycloakAuthFailedException();
        }
        if (status == HttpStatus.FORBIDDEN) {
            return new KeycloakForbiddenException();
        }
        if (status == HttpStatus.NOT_FOUND) {
            return new KeycloakUserNotFoundException();
        }

        return new KeycloakUnavailableException();
    }
}

