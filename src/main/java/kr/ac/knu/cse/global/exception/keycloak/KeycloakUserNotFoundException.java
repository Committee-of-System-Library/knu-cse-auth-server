package kr.ac.knu.cse.global.exception.keycloak;

import static kr.ac.knu.cse.global.exception.keycloak.KeycloakErrorCode.KEYCLOAK_AUTH_FAILED;
import static kr.ac.knu.cse.global.exception.keycloak.KeycloakErrorCode.KEYCLOAK_USER_NOT_FOUND;

import kr.ac.knu.cse.global.exception.BusinessException;

public class KeycloakUserNotFoundException extends BusinessException {
    public KeycloakUserNotFoundException() {
        super(KEYCLOAK_USER_NOT_FOUND);
    }
}
