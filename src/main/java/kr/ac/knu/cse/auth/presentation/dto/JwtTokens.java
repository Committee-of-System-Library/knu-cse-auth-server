package kr.ac.knu.cse.auth.presentation.dto;

import jakarta.validation.constraints.NotNull;
import kr.ac.knu.cse.token.domain.Token;
import lombok.Builder;

@Builder
public record JwtTokens(
	@NotNull Token accessToken,
	@NotNull Token refreshToken
) {
}
