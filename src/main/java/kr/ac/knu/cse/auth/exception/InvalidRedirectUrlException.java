package kr.ac.knu.cse.auth.exception;

import kr.ac.knu.cse.global.exception.support.business.BadRequestException;

public class InvalidRedirectUrlException extends BadRequestException {
	private static final String errorMsg = "INVALID_REDIRECT_URL";

	public InvalidRedirectUrlException() {
		super(errorMsg);
	}
}
