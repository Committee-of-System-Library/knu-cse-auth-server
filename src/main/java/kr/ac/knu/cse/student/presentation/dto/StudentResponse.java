package kr.ac.knu.cse.student.presentation.dto;

import kr.ac.knu.cse.student.domain.Major;
import kr.ac.knu.cse.student.domain.Role;

public record StudentResponse(
        String studentNumber,
        String name,
        String major,
        String role
) {
    public static StudentResponse of(
            final String studentNumber,
            final String name,
            final Major major,
            final Role role
    ) {
        return new StudentResponse(
                studentNumber,
                name,
                major.name(),
                role.name()
        );
    }
}
