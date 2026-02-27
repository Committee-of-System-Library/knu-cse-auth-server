package kr.ac.knu.cse.global.exception.common;

import kr.ac.knu.cse.global.exception.BusinessException;

public class InternalServerErrorException extends BusinessException {

    public InternalServerErrorException() {
        super(CommonErrorCode.INTERNAL_ERROR);
    }
}
