package kr.ac.knu.cse.client.presentation.dto;

import kr.ac.knu.cse.client.domain.AuthClient;
import kr.ac.knu.cse.client.domain.AuthClientStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AuthClientResponse {

	private final Long clientId;
	private final String clientName;
	private final String clientDescription;
	private final List<String> allowedDomains;
	private final AuthClientStatus status;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private AuthClientResponse(Long clientId, String clientName, String clientDescription,
							   List<String> allowedDomains, AuthClientStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.clientId = clientId;
		this.clientName = clientName;
		this.clientDescription = clientDescription;
		this.allowedDomains = allowedDomains;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static AuthClientResponse from(AuthClient authClient) {
		return new AuthClientResponse(
			authClient.getClientId(),
			authClient.getClientName(),
			authClient.getClientDescription(),
			authClient.getAllowedDomains(),
			authClient.getStatus(),
			authClient.getCreatedAt(),
			authClient.getUpdatedAt()
		);
	}
}
