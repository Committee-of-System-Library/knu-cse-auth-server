package kr.ac.knu.cse.application.dto;

import java.time.LocalDateTime;

public record HandoutScanResult(
        Result result,
        String studentNumber,
        String name,
        String major,
        Boolean paid,
        LocalDateTime receivedAt
) {
    public enum Result {
        OK,
        DUPLICATE,
        UNPAID,
        NOT_FOUND
    }

    public static HandoutScanResult ok(String studentNumber, String name, String major, LocalDateTime receivedAt) {
        return new HandoutScanResult(Result.OK, studentNumber, name, major, true, receivedAt);
    }

    public static HandoutScanResult duplicate(String studentNumber, String name, String major, LocalDateTime receivedAt) {
        return new HandoutScanResult(Result.DUPLICATE, studentNumber, name, major, null, receivedAt);
    }

    public static HandoutScanResult unpaid(String studentNumber, String name, String major) {
        return new HandoutScanResult(Result.UNPAID, studentNumber, name, major, false, null);
    }

    public static HandoutScanResult notFound(String studentNumber) {
        return new HandoutScanResult(Result.NOT_FOUND, studentNumber, null, null, null, null);
    }
}
