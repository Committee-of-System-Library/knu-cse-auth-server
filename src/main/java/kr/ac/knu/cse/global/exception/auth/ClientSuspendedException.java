package kr.ac.knu.cse.global.exception.auth;

import kr.ac.knu.cse.global.exception.BusinessException;

public class ClientSuspendedException extends BusinessException {

    public ClientSuspendedException() {
        super(AuthErrorCode.CLIENT_SUSPENDED);
    }
}
