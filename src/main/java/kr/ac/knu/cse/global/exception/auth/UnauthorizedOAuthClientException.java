package kr.ac.knu.cse.global.exception.auth;

import static kr.ac.knu.cse.global.exception.auth.AuthErrorCode.UNAUTHORIZED_OAUTH_CLIENT;

import kr.ac.knu.cse.global.exception.BusinessException;

public class UnauthorizedOAuthClientException extends BusinessException {
    public UnauthorizedOAuthClientException() {
        super(UNAUTHORIZED_OAUTH_CLIENT);
    }
}
