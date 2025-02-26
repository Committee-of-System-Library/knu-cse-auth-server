package kr.ac.knu.cse.token.exception;

import kr.ac.knu.cse.global.exception.support.business.UnauthorizedException;

public class TokenExpiredException extends UnauthorizedException {
	private static final String errorMsg = "TOKEN_EXPIRED";
	public TokenExpiredException() {
		super(errorMsg);
	}
}
