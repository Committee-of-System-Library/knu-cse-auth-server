package kr.ac.knu.cse.presentation.dto;

import jakarta.validation.constraints.NotNull;
import kr.ac.knu.cse.domain.student.UserType;

public record UserTypeChangeRequest(
        @NotNull UserType userType
) {
}
