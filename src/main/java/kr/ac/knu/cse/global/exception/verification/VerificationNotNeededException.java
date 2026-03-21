package kr.ac.knu.cse.global.exception.verification;

import kr.ac.knu.cse.global.exception.BusinessException;

public class VerificationNotNeededException extends BusinessException {

    public VerificationNotNeededException() {
        super(VerificationErrorCode.VERIFICATION_NOT_NEEDED);
    }
}
