package kr.ac.knu.cse.provider.exception;

import kr.ac.knu.cse.global.exception.support.business.BadRequestException;

public class NotConnectedStudentException extends BadRequestException {
	private static final String errorMsg = "NOT_CONNECTED_STUDENT";

	public NotConnectedStudentException() {
		super(errorMsg);
	}
}
