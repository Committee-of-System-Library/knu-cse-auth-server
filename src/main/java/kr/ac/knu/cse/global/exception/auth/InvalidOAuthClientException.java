package kr.ac.knu.cse.global.exception.auth;

import static kr.ac.knu.cse.global.exception.auth.AuthErrorCode.INVALID_OAUTH_CLIENT;

import kr.ac.knu.cse.global.exception.BusinessException;

public class InvalidOAuthClientException extends BusinessException {
    public InvalidOAuthClientException() {
        super(INVALID_OAUTH_CLIENT);
    }
}
