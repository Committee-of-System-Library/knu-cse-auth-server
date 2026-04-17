package kr.ac.knu.cse.application.dto;

import java.time.LocalDateTime;
import kr.ac.knu.cse.domain.snack.SnackHandout;

public record SnackHandoutResponse(
        Long id,
        String studentNumber,
        String name,
        String major,
        LocalDateTime receivedAt
) {
    public static SnackHandoutResponse from(SnackHandout handout) {
        return new SnackHandoutResponse(
                handout.getId(),
                handout.getStudentNumber(),
                handout.getName(),
                handout.getMajor(),
                handout.getReceivedAt()
        );
    }
}
