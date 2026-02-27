package kr.ac.knu.cse.global.exception.keycloak;

import static kr.ac.knu.cse.global.exception.keycloak.KeycloakErrorCode.KEYCLOAK_AUTH_FAILED;

import kr.ac.knu.cse.global.exception.BusinessException;

public class KeycloakAuthFailedException extends BusinessException {
    public KeycloakAuthFailedException() {
        super(KEYCLOAK_AUTH_FAILED);
    }
}
