package kr.ac.knu.cse.presentation.dto;

public record SecretRegenerateResponse(
        String clientId,
        String clientSecret
) {
}
