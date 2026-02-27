package kr.ac.knu.cse.global.exception.common;

import kr.ac.knu.cse.global.exception.BusinessException;

public class InvalidRequestException extends BusinessException {
    public InvalidRequestException() {
        super(CommonErrorCode.INVALID_REQUEST);
    }
}
