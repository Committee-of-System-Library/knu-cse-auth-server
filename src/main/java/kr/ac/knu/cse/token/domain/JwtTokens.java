package kr.ac.knu.cse.token.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record JwtTokens(
	@NotNull Token accessToken,
	@NotNull Token refreshToken
) {
}
