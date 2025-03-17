package kr.ac.knu.cse.token.exception;

import kr.ac.knu.cse.global.exception.support.business.UnauthorizedException;

public class TokenInvalidException extends UnauthorizedException {
	private static final String errorMsg = "TOKEN_INVALID";

	public TokenInvalidException() {
		super(errorMsg);
	}
}
