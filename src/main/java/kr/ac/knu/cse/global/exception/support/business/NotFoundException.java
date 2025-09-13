package kr.ac.knu.cse.global.exception.support.business;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class NotFoundException extends ApplicationLogicException {
	private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

	public NotFoundException(final String errorMsg) {
		super(errorMsg);
	}
}
