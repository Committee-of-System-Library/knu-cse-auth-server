package kr.ac.knu.cse.dues.presentation.dto;

import java.time.LocalDateTime;

public record DuesListResponse(
        Long duesId,
        String studentName,
        String studentNumber,
        String depositorName,
        Integer amount,
        Integer remainingSemesters,
        LocalDateTime submittedAt
) {
}
