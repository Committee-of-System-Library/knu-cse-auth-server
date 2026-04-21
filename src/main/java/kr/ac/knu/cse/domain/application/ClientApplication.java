package kr.ac.knu.cse.domain.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "client_application")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_name", nullable = false, length = 100)
    private String appName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "client_id", unique = true)
    private String clientId;

    @JsonIgnore
    @Column(name = "client_secret_hash")
    private String clientSecretHash;

    @Column(name = "redirect_uris", nullable = false, columnDefinition = "JSON")
    private String redirectUris;

    @Column(name = "homepage_url", length = 500)
    private String homepageUrl;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ClientApplicationStatus status;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @JsonIgnore
    @Column(name = "keycloak_client_uuid")
    private String keycloakClientUuid;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static ClientApplication of(
            String appName,
            String description,
            String redirectUris,
            String homepageUrl,
            Long ownerId
    ) {
        return new ClientApplication(
                null,
                appName,
                description,
                null,
                null,
                redirectUris,
                homepageUrl,
                ownerId,
                ClientApplicationStatus.PENDING,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public void updateInfo(String appName, String description, String redirectUris, String homepageUrl) {
        this.appName = appName;
        this.description = description;
        this.redirectUris = redirectUris;
        this.homepageUrl = homepageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void approve(String clientId, String clientSecretHash, String keycloakClientUuid) {
        this.status = ClientApplicationStatus.APPROVED;
        this.clientId = clientId;
        this.clientSecretHash = clientSecretHash;
        this.keycloakClientUuid = keycloakClientUuid;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        this.status = ClientApplicationStatus.REJECTED;
        this.rejectionReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        this.status = ClientApplicationStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateSecret(String clientSecretHash) {
        this.clientSecretHash = clientSecretHash;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOwner(Long studentId) {
        return this.ownerId.equals(studentId);
    }

    public boolean isApproved() {
        return this.status == ClientApplicationStatus.APPROVED;
    }

    public boolean isPending() {
        return this.status == ClientApplicationStatus.PENDING;
    }
}
