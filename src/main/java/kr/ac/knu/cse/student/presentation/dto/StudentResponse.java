package kr.ac.knu.cse.student.presentation.dto;

import kr.ac.knu.cse.student.domain.Major;
import kr.ac.knu.cse.student.domain.Role;

public record StudentResponse(
    Long studentId,
    String studentNumber,
    String name,
    String major,
    String role
) {
    public static StudentResponse of(
        final Long studentId,
        final String studentNumber,
        final String name,
        final Major major,
        final Role role
    ) {
        return new StudentResponse(
            studentId,
                studentNumber,
                name,
                major.name(),
                role.name()
        );
    }
}
