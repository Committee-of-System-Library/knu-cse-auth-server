package kr.ac.knu.cse.dues.presentation.dto;

import kr.ac.knu.cse.dues.domain.Dues;
import kr.ac.knu.cse.student.domain.Student;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DuesReadDto(
        Long duesId,
        Long studentId,
        String depositorName,
        Integer amount,
        Integer remainingSemesters,
        LocalDateTime submittedAt
) {
    public static DuesReadDto from(Student student, Dues dues) {
        return DuesReadDto.builder()
                .duesId(dues.getId())
                .studentId(student.getId())
                .depositorName(dues.getDepositorName())
                .amount(dues.getAmount())
                .remainingSemesters(dues.getRemainingSemesters())
                .submittedAt(dues.getSubmittedAt())
                .build();
    }
}
