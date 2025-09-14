package kr.ac.knu.cse.student.application.dto;

import kr.ac.knu.cse.student.domain.Major;
import kr.ac.knu.cse.student.domain.Role;

public record UpdateStudentDto(
        String studentNumber,
        String name,
        Major major,
        Role role
) {
}
