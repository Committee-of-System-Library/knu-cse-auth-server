package kr.ac.knu.cse.global.exception.snack;

import kr.ac.knu.cse.global.exception.BusinessException;

public class SnackEventNotOpenException extends BusinessException {
    public SnackEventNotOpenException() {
        super(SnackErrorCode.SNACK_EVENT_NOT_OPEN);
    }
}
