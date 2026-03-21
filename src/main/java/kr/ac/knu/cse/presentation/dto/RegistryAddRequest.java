package kr.ac.knu.cse.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistryAddRequest(
        @NotBlank String studentNumber,
        @NotBlank String name,
        String major,
        Integer grade
) {
}
