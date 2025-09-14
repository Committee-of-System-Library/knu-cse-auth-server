package kr.ac.knu.cse.student.application.dto;

import kr.ac.knu.cse.student.domain.Major;
import kr.ac.knu.cse.student.domain.Role;
import kr.ac.knu.cse.student.domain.Student;

public record SaveStudentDto(
        String studentNumber,
        String name,
        Major major,
        Role role
) {
    public Student of() {
        return Student.builder()
                .studentNumber(studentNumber)
                .name(name)
                .major(major)
                .role(role)
                .build();
    }
}
