package kr.ac.knu.cse.global.exception.verification;

import kr.ac.knu.cse.global.exception.BusinessException;

public class VerificationNotPendingException extends BusinessException {

    public VerificationNotPendingException() {
        super(VerificationErrorCode.VERIFICATION_NOT_PENDING);
    }
}
