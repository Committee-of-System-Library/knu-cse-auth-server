package kr.ac.knu.cse.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SnackEventCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 10) String semester,
        @NotNull Boolean requiresPayment
) {
}
