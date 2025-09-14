package kr.ac.knu.cse.provider.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PostCreateProviderReq(
        @Email @NotBlank String email,
        @NotBlank String providerName,
        @NotBlank String providerKey,
        Long studentId // nullable
) {
}
