package kr.ac.knu.cse.auth.presentation.dto;

import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.student.domain.Student;

public record TokenInfoDto(
        Long studentId,
        String studentNumber,
        String email,
        String name,
        String role
) {
    public static TokenInfoDto from(Student student, Provider provider) {
        return new TokenInfoDto(
                student.getId(),
                student.getStudentNumber(),
                provider.getEmail(),
                student.getName(),
                student.getRole().name()
        );
    }
}
