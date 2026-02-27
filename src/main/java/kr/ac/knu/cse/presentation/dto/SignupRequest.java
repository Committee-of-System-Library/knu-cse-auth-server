package kr.ac.knu.cse.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.ac.knu.cse.domain.student.Grade;

public record SignupRequest(
        @NotBlank String studentNumber,
        @NotBlank String major,
        @NotNull Grade grade
) {
}
