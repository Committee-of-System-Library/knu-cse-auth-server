package kr.ac.knu.cse.global.exception.keycloak;

import static kr.ac.knu.cse.global.exception.keycloak.KeycloakErrorCode.KEYCLOAK_AUTH_FAILED;
import static kr.ac.knu.cse.global.exception.keycloak.KeycloakErrorCode.KEYCLOAK_FORBIDDEN;

import kr.ac.knu.cse.global.exception.BusinessException;

public class KeycloakForbiddenException extends BusinessException {
    public KeycloakForbiddenException() {
        super(KEYCLOAK_FORBIDDEN);
    }
}
