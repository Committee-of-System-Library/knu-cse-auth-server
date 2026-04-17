package kr.ac.knu.cse.global.exception.snack;

import kr.ac.knu.cse.global.exception.BusinessException;

public class SnackEventNotFoundException extends BusinessException {
    public SnackEventNotFoundException() {
        super(SnackErrorCode.SNACK_EVENT_NOT_FOUND);
    }
}
