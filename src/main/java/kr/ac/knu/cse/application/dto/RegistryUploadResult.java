package kr.ac.knu.cse.application.dto;

public record RegistryUploadResult(
        int totalRows,
        int insertedCount,
        int updatedCount,
        int errorCount
) {
}
