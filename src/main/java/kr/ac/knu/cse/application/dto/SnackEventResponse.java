package kr.ac.knu.cse.application.dto;

import java.time.LocalDateTime;
import kr.ac.knu.cse.domain.snack.SnackEvent;
import kr.ac.knu.cse.domain.snack.SnackEventStatus;

public record SnackEventResponse(
        Long id,
        String name,
        String semester,
        boolean requiresPayment,
        SnackEventStatus status,
        LocalDateTime openedAt,
        LocalDateTime closedAt,
        String openedByStudentNumber,
        long handoutCount
) {
    public static SnackEventResponse of(SnackEvent event, long handoutCount) {
        return new SnackEventResponse(
                event.getId(),
                event.getName(),
                event.getSemester(),
                event.isRequiresPayment(),
                event.getStatus(),
                event.getOpenedAt(),
                event.getClosedAt(),
                event.getOpenedByStudentNumber(),
                handoutCount
        );
    }
}
