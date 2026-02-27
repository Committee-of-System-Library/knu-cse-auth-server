package kr.ac.knu.cse.global.exception.provisioning;

import static kr.ac.knu.cse.global.exception.provisioning.ProvisioningErrorCode.STUDENT_NOT_FOUND;

import kr.ac.knu.cse.global.exception.BusinessException;

public class StudentNotFoundException extends BusinessException {
    public StudentNotFoundException() {
        super(STUDENT_NOT_FOUND);
    }
}
