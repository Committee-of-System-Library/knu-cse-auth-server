package kr.ac.knu.cse.global.exception.auth;

import kr.ac.knu.cse.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_REDIRECT_URL("AUTH_001", "잘못된 redirect url입니다."),
    INVALID_OIDC_USER("AUTH_002", "인증 사용자 정보가 올바르지 않습니다."),
    MISSING_EMAIL("AUTH_003", "이메일 정보가 누락되었습니다."),
    INVALID_EMAIL_DOMAIN("AUTH_004", "허용되지 않은 이메일 도메인입니다."),
    ALREADY_SIGNED_UP("AUTH_005", "이미 회원가입 된 학생입니다."),
    INVALID_OAUTH_CLIENT("AUTH_006", "인증 서버 설정 오류입니다."),
    UNAUTHORIZED_OAUTH_CLIENT("AUTH_007", "허용되지 않은 인증 클라이언트입니다."),
    INVALID_SESSION("AUTH_008", "유효하지 않은 세션입니다."),
    OAUTH_AUTHENTICATION_FAILED("AUTH_009", "OAuth 인증에 실패했습니다.");

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
