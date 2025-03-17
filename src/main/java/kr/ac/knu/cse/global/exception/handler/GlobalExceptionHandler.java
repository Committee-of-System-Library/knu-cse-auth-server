package kr.ac.knu.cse.global.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import kr.ac.knu.cse.global.api.ApiErrorResult;
import kr.ac.knu.cse.global.exception.BaseExceptionHandler;
import kr.ac.knu.cse.global.exception.support.GlobalException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler<GlobalException> {

	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<ApiErrorResult> handleCustomException(GlobalException exception) {
		return handleException(exception, exception.getHttpStatus(), exception.getErrorMsg());
	}
}
