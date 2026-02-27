package kr.ac.knu.cse.global.exception.keycloak;

import kr.ac.knu.cse.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum KeycloakErrorCode implements ErrorCode {
    KEYCLOAK_AUTH_FAILED("KC_001", "Keycloak 관리자 인증이  실패했습니다."),
    KEYCLOAK_FORBIDDEN("KC_002", "Keycloak 관리자 접근이 거부됐습니다."),
    KEYCLOAK_USER_NOT_FOUND("KC_003", "Keycloak에 계정이 존재하지 않습니다."),
    KEYCLOAK_UNAVAILABLE("KC_004", "Keycloak에 접근할 수 없습니다.");

    private final String code;
    private final String message;
    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
