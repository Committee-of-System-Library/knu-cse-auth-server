package kr.ac.knu.cse.dues.presentation.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateDuesPostReq(
	@NotNull(message = "Student ID is required")
	Long studentId,

	@NotBlank(message = "Depositor name is required")
	String depositorName,

	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be positive")
	Integer amount,

	@NotNull(message = "Remaining semesters is required")
	@PositiveOrZero(message = "Remaining semesters must be zero or positive")
	Integer remainingSemesters,

	@NotNull(message = "Submitted date/time is required")
	LocalDateTime submittedAt
) {
}
