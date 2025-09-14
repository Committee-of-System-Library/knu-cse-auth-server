package kr.ac.knu.cse.admin.presentation.dto;

public record AdminStatisticsResponse(
        long totalStudents,
        long paidDues,
        long qrScans,
        long providers
) {
}
