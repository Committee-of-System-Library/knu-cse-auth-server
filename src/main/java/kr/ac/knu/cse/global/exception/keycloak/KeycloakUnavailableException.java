package kr.ac.knu.cse.global.exception.keycloak;

import static kr.ac.knu.cse.global.exception.keycloak.KeycloakErrorCode.KEYCLOAK_AUTH_FAILED;
import static kr.ac.knu.cse.global.exception.keycloak.KeycloakErrorCode.KEYCLOAK_UNAVAILABLE;

import kr.ac.knu.cse.global.exception.BusinessException;

public class KeycloakUnavailableException extends BusinessException {
    public KeycloakUnavailableException() {
        super(KEYCLOAK_UNAVAILABLE);
    }
}
