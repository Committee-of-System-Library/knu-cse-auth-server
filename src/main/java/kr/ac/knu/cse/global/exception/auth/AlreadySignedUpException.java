package kr.ac.knu.cse.global.exception.auth;

import kr.ac.knu.cse.global.exception.BusinessException;

public class AlreadySignedUpException extends BusinessException {

    public AlreadySignedUpException () {
        super(AuthErrorCode.ALREADY_SIGNED_UP);
    }
}
