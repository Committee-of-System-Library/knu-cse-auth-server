package kr.ac.knu.cse.global.exception.provisioning;

import kr.ac.knu.cse.global.exception.BusinessException;

public class InvalidRoleException extends BusinessException {
    public InvalidRoleException() {
        super(ProvisioningErrorCode.INVALID_ROLE);
    }
}
