package kr.ac.knu.cse.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record VerificationSubmitRequest(
        @NotBlank String studentNumber,
        String evidence
) {
}
