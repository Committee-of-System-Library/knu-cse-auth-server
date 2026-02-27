package kr.ac.knu.cse.global.exception.provisioning;

import kr.ac.knu.cse.global.exception.BusinessException;

public class ProviderWithoutStudentException extends BusinessException {
    public ProviderWithoutStudentException() {
        super(ProvisioningErrorCode.PROVIDER_HAS_NO_STUDENT);
    }
}
