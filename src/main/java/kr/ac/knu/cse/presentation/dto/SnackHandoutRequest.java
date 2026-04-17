package kr.ac.knu.cse.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record SnackHandoutRequest(
        @NotBlank String studentNumber
) {
}
