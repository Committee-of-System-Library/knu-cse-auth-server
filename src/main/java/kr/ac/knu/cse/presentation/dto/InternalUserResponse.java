package kr.ac.knu.cse.presentation.dto;

import kr.ac.knu.cse.domain.role.Role;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.student.UserType;

public record InternalUserResponse(
        String studentNumber,
        String name,
        UserType userType,
        Role role,
        String major
) {
    public static InternalUserResponse from(Student student) {
        return new InternalUserResponse(
                student.getStudentNumber(),
                student.getName(),
                student.getUserType(),
                student.getRole(),
                student.getMajor()
        );
    }
}
