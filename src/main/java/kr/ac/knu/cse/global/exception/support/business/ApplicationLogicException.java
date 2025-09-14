package kr.ac.knu.cse.global.exception.support.business;

import kr.ac.knu.cse.global.exception.support.GlobalException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApplicationLogicException extends GlobalException {
    private final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private final String errorMsg;

    public ApplicationLogicException(final String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }
}
