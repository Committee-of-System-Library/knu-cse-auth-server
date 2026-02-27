package kr.ac.knu.cse.global.exception;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final String message;
    private final String code;

    public BusinessException(ErrorCode errorCode) {
        this.message = errorCode.message();
        this.code = errorCode.code();
    }
}
