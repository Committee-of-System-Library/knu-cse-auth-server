package kr.ac.knu.cse.dues.exception;

import kr.ac.knu.cse.global.exception.support.business.NotFoundException;

public class DuesNotFoundException extends NotFoundException {
    private static final String errorMsg = "DUES_NOT_FOUND";

    public DuesNotFoundException() {
        super(errorMsg);
    }
}
