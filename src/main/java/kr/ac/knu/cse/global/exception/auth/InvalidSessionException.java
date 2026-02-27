package kr.ac.knu.cse.global.exception.auth;

import static kr.ac.knu.cse.global.exception.auth.AuthErrorCode.INVALID_SESSION;

import kr.ac.knu.cse.global.exception.BusinessException;

public class InvalidSessionException extends BusinessException {
    public InvalidSessionException() {
        super(INVALID_SESSION);
    }
}
