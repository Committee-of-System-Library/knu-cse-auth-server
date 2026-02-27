package kr.ac.knu.cse.global.exception.internal;


import static kr.ac.knu.cse.global.exception.internal.InternalErrorCode.INTERNAL_API_FORBIDDEN;

import kr.ac.knu.cse.global.exception.BusinessException;

public class InternalApiForbiddenException extends BusinessException {
    public InternalApiForbiddenException() {
        super(INTERNAL_API_FORBIDDEN);
    }
}
