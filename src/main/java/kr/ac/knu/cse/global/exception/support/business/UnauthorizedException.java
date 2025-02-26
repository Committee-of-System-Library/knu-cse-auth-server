package kr.ac.knu.cse.global.exception.support.business;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public abstract class UnauthorizedException extends ApplicationLogicException {
	private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
	public UnauthorizedException(final String errorMsg) {
		super(errorMsg);
	}
}
