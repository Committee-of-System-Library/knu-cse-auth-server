package kr.ac.knu.cse.global.exception.provisioning;

import static kr.ac.knu.cse.global.exception.provisioning.ProvisioningErrorCode.INVALID_STUDENT_ID;

import kr.ac.knu.cse.global.exception.BusinessException;

public class InvalidStudentIdException extends BusinessException {
    public InvalidStudentIdException() {
        super(INVALID_STUDENT_ID);
    }
}
