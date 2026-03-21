package kr.ac.knu.cse.presentation.dto;

import jakarta.validation.constraints.NotNull;
import kr.ac.knu.cse.domain.role.Role;

public record RoleChangeRequest(
        @NotNull Role role
) {
}
