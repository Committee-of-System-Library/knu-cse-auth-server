package kr.ac.knu.cse.global.exception.support.business;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BadRequestException extends ApplicationLogicException {
	private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

	public BadRequestException(final String errorMsg) {
		super(errorMsg);
	}
}
