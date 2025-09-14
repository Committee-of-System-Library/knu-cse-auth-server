package kr.ac.knu.cse.student.exception;

import kr.ac.knu.cse.global.exception.support.business.NotFoundException;

public class StudentNotFoundException extends NotFoundException {
    private static final String errorMsg = "STUDENT_NOT_FOUND";

    public StudentNotFoundException() {
        super(errorMsg);
    }
}
