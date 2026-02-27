package kr.ac.knu.cse.global.exception.auth;

import static kr.ac.knu.cse.global.exception.auth.AuthErrorCode.OAUTH_AUTHENTICATION_FAILED;

import kr.ac.knu.cse.global.exception.BusinessException;

public class OAuthAuthenticationFailedException extends BusinessException {
    public OAuthAuthenticationFailedException() {
        super(OAUTH_AUTHENTICATION_FAILED);
    }
}
