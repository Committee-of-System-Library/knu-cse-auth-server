package kr.ac.knu.cse.global.exception.verification;

import kr.ac.knu.cse.global.exception.BusinessException;

public class VerificationAlreadyPendingException extends BusinessException {

    public VerificationAlreadyPendingException() {
        super(VerificationErrorCode.VERIFICATION_ALREADY_PENDING);
    }
}
