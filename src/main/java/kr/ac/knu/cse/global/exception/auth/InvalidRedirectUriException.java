package kr.ac.knu.cse.global.exception.auth;

import kr.ac.knu.cse.global.exception.BusinessException;

public class InvalidRedirectUriException extends BusinessException {

    public InvalidRedirectUriException() {
        super(AuthErrorCode.INVALID_REDIRECT_URL);
    }
}
