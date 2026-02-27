package kr.ac.knu.cse.global.exception.auth;

import kr.ac.knu.cse.global.exception.BusinessException;
import kr.ac.knu.cse.global.exception.ErrorCode;

public class InvalidOidcUserException extends BusinessException {
    public InvalidOidcUserException() {
        super(AuthErrorCode.INVALID_OIDC_USER);
    }
}
