package kr.ac.knu.cse.global.exception.auth;

import kr.ac.knu.cse.global.exception.BusinessException;

public class InvalidClientException extends BusinessException {

    public InvalidClientException() {
        super(AuthErrorCode.INVALID_CLIENT);
    }
}
