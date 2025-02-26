package kr.ac.knu.cse.token.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record Token(
	@NotNull String grantType,
	@NotNull TokenType tokenType,
	@NotNull String value
) {}
