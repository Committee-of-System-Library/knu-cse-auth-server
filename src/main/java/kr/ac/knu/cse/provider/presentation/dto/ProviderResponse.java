package kr.ac.knu.cse.provider.presentation.dto;

public record ProviderResponse(
	Long id,
	String email,
	String providerName,
	String providerKey,
	Long studentId
) {
}
