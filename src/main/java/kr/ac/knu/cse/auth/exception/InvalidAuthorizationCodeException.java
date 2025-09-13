package kr.ac.knu.cse.auth.exception;

import kr.ac.knu.cse.global.exception.support.business.BadRequestException;

public class InvalidAuthorizationCodeException extends BadRequestException {
    private static final String errorMsg = "INVALID_AUTHORIZATION_CODE";

	public InvalidAuthorizationCodeException() {
		super(errorMsg);
	}
}
