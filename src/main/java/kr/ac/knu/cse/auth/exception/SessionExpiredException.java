package kr.ac.knu.cse.auth.exception;

import kr.ac.knu.cse.global.exception.support.business.BadRequestException;

public class SessionExpiredException extends BadRequestException {
    private static final String errorMsg = "SESSION_EXPIRED";

    public SessionExpiredException() {
        super(errorMsg);
    }
}
