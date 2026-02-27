package kr.ac.knu.cse.global.exception.auth;

import kr.ac.knu.cse.global.exception.BusinessException;
import kr.ac.knu.cse.global.exception.ErrorCode;

public class InvalidEmailDomainException extends BusinessException {
    public InvalidEmailDomainException() {
        super(AuthErrorCode.INVALID_EMAIL_DOMAIN);
    }
}
