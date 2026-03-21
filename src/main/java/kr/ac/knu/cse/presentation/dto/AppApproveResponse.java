package kr.ac.knu.cse.presentation.dto;

public record AppApproveResponse(
        Long id,
        String clientId,
        String clientSecret,
        String jwtSecret
) {
}
