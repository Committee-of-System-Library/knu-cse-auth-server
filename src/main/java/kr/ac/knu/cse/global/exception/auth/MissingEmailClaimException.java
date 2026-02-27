package kr.ac.knu.cse.global.exception.auth;

import kr.ac.knu.cse.global.exception.BusinessException;
import kr.ac.knu.cse.global.exception.ErrorCode;

public class MissingEmailClaimException extends BusinessException {
    public MissingEmailClaimException() {
        super(AuthErrorCode.MISSING_EMAIL);
    }
}
