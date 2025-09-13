package kr.ac.knu.cse.auth.presentation.dto;

import kr.ac.knu.cse.student.domain.Student;

public record AdditionalInfoResponse(String name, String studentNumber) {
    public static AdditionalInfoResponse of(Student student) {
        return new AdditionalInfoResponse(
                student.getName(),
                student.getStudentNumber()
        );
    }
}
