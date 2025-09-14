package kr.ac.knu.cse.qr.presentation.dto;

public record ScannedStudent(
        String studentNumber,
        String studentName,
        boolean duesPaid
) {
}
