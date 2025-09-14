package kr.ac.knu.cse.student.presentation.dto;

import kr.ac.knu.cse.student.domain.Major;
import kr.ac.knu.cse.student.domain.Role;

public record StudentResponse(
        Long studentId,
        String studentNumber,
        String name,
        String major,
        String role,
        boolean hasDues
) {
    public static StudentResponse of(
            Long studentId,
            String studentNumber,
            String name,
            Major major,
            Role role,
            boolean hasDues
    ) {
        return new StudentResponse(
                studentId,
                studentNumber,
                name,
                major.name(),
                role.name(),
                hasDues
        );
    }
}
