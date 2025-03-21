package kr.ac.knu.cse.student.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.ac.knu.cse.student.domain.Major;
import kr.ac.knu.cse.student.domain.Role;

public record PostCreateStudentReq(
	@NotBlank(message = "Student number is required")
	@Size(max = 15, message = "Student number must be at most 15 characters")
	String studentNumber,

	@NotBlank(message = "Name is required")
	@Size(max = 50, message = "Name must be at most 50 characters")
	String name,

	@NotNull(message = "Major is required")
	Major major,

	@NotNull(message = "Role is required")
	Role role
) {
}
