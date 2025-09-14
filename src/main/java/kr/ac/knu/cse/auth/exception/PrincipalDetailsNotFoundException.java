package kr.ac.knu.cse.auth.exception;

import kr.ac.knu.cse.global.exception.support.business.UnauthorizedException;

public class PrincipalDetailsNotFoundException extends UnauthorizedException {
    private static final String errorMsg = "PRINCIPAL_DETAILS_NOT_FOUND";

    public PrincipalDetailsNotFoundException() {
        super(errorMsg);
    }
}
