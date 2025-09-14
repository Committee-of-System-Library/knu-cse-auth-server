package kr.ac.knu.cse.client.presentation.dto;

import kr.ac.knu.cse.client.domain.AuthClient;
import kr.ac.knu.cse.client.domain.AuthClientStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AuthClientDetailResponse {

	private final Long clientId;
	private final String clientName;
	private final String clientDescription;
	private final String jwtSecret;
	private final List<String> allowedDomains;
	private final AuthClientStatus status;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private AuthClientDetailResponse(Long clientId, String clientName, String clientDescription,
									 String jwtSecret, List<String> allowedDomains,
									 AuthClientStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.clientId = clientId;
		this.clientName = clientName;
		this.clientDescription = clientDescription;
		this.jwtSecret = jwtSecret;
		this.allowedDomains = allowedDomains;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static AuthClientDetailResponse from(AuthClient authClient) {
		return new AuthClientDetailResponse(
			authClient.getClientId(),
			authClient.getClientName(),
			authClient.getClientDescription(),
			authClient.getJwtSecret(),
			authClient.getAllowedDomains(),
			authClient.getStatus(),
			authClient.getCreatedAt(),
			authClient.getUpdatedAt()
		);
	}
}
