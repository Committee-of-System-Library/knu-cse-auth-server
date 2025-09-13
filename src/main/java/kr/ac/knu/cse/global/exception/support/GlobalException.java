package kr.ac.knu.cse.global.exception.support;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GlobalException extends RuntimeException {
	private final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	private final String errorMsg;

	public GlobalException(String errorMsg) {
		super(errorMsg);
		this.errorMsg = errorMsg;
	}
}
