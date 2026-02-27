package kr.ac.knu.cse.global.exception.common;

import kr.ac.knu.cse.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INTERNAL_ERROR("COMMON_001", "서버 내부 오류가 발생했습니다."),
    INVALID_REQUEST("COMMON_002", "잘못된 요청입니다.");

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
