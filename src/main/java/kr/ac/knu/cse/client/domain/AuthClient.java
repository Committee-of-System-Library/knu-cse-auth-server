package kr.ac.knu.cse.client.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import kr.ac.knu.cse.global.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Entity
@Table(name = "auth_clients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthClient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "client_name", nullable = false, unique = true, length = 100)
    private String clientName;

    @Column(name = "client_description", length = 500)
    private String clientDescription;

    @Column(name = "jwt_secret", nullable = false, length = 500)
    private String jwtSecret;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "auth_client_allowed_domains",
            joinColumns = @JoinColumn(name = "client_id")
    )
    @Column(name = "domain")
    private List<String> allowedDomains;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AuthClientStatus status = AuthClientStatus.ACTIVE;

    @Builder
    public AuthClient(String clientName, String clientDescription, List<String> allowedDomains) {
        this.clientName = clientName;
        this.clientDescription = clientDescription;
        this.jwtSecret = generateSecret();
        this.allowedDomains = allowedDomains;
        this.status = AuthClientStatus.ACTIVE;
    }

    private String generateSecret() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] secretBytes = new byte[32];
        secureRandom.nextBytes(secretBytes);
        return Base64.getEncoder().encodeToString(secretBytes);
    }

    public void regenerateSecret() {
        this.jwtSecret = generateSecret();
    }

    public void updateClientInfo(String clientName, String clientDescription, List<String> allowedDomains) {
        this.clientName = clientName;
        this.clientDescription = clientDescription;
        this.allowedDomains = allowedDomains;
    }

    public void deactivate() {
        this.status = AuthClientStatus.INACTIVE;
    }

    public void activate() {
        this.status = AuthClientStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == AuthClientStatus.ACTIVE;
    }

    public boolean isAllowedDomain(String domain) {
        if (domain == null || allowedDomains == null) {
            return false;
        }
        return allowedDomains.stream()
                .anyMatch(domain::startsWith);
    }

    public boolean isValidRedirectRequest(String redirectUrl, String origin) {
        if (redirectUrl == null || origin == null) {
            return false;
        }

        try {
            java.net.URL redirectUrlParsed = new java.net.URL(redirectUrl);
            java.net.URL originParsed = new java.net.URL(origin);

            return redirectUrlParsed.getHost().equals(originParsed.getHost()) &&
                    redirectUrlParsed.getPort() == originParsed.getPort();
        } catch (Exception e) {
            return false;
        }
    }
}
