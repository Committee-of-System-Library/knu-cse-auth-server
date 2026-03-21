package kr.ac.knu.cse.global.exception.verification;

import kr.ac.knu.cse.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VerificationErrorCode implements ErrorCode {

    VERIFICATION_ALREADY_PENDING("VERIFY_001", "이미 심사 중인 학부생 인증 요청이 있습니다."),
    VERIFICATION_NOT_NEEDED("VERIFY_002", "이미 학부생으로 인증된 사용자입니다."),
    VERIFICATION_NOT_FOUND("VERIFY_003", "학부생 인증 요청을 찾을 수 없습니다."),
    VERIFICATION_NOT_PENDING("VERIFY_004", "이미 처리된 인증 요청입니다.");

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
