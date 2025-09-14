package kr.ac.knu.cse.client.exception;

import kr.ac.knu.cse.global.exception.support.business.UnauthorizedException;

public class UnauthorizedServiceAccessException extends UnauthorizedException {
	public UnauthorizedServiceAccessException() {
		super("허용되지 않은 도메인에서의 접근입니다.");
	}
}
