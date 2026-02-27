package kr.ac.knu.cse.global.exception.provisioning;

import static kr.ac.knu.cse.global.exception.provisioning.ProvisioningErrorCode.INVALID_STUDENT_NUMBER;

import kr.ac.knu.cse.global.exception.BusinessException;

public class InvalidStudentNumberException extends BusinessException {
    public InvalidStudentNumberException() {
        super(INVALID_STUDENT_NUMBER);
    }
}
