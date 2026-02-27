package kr.ac.knu.cse.global.exception.internal;

import kr.ac.knu.cse.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InternalErrorCode implements ErrorCode {
    INTERNAL_API_FORBIDDEN("INTERNAL_001", "내부 API 접근 권한이 없습니다.");
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
