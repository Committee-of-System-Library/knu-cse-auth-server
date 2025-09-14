package kr.ac.knu.cse.dues.application.dto;

import java.time.LocalDateTime;

public record CreateDuesDto(
        Long studentId,
        String depositorName,
        Integer amount,
        Integer remainingSemesters,
        LocalDateTime submittedAt
) {
}
