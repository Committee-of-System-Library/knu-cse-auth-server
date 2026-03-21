package kr.ac.knu.cse.global.exception.auth;

import kr.ac.knu.cse.global.exception.BusinessException;

public class AdminAccessDeniedException extends BusinessException {

    public AdminAccessDeniedException() {
        super(AuthErrorCode.ADMIN_ACCESS_DENIED);
    }
}
