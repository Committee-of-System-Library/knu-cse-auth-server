package kr.ac.knu.cse.domain.client;

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
import java.util.Set;
import kr.ac.knu.cse.global.base.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "auth_clients")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthClient extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long id;

    @Column(name = "client_name", nullable = false, unique = true)
    private String clientName;

    @Column(name = "client_description")
    private String clientDescription;

    @Column(name = "jwt_secret", nullable = false)
    private String jwtSecret;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AuthClientStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "auth_client_allowed_domains",
            joinColumns = @JoinColumn(name = "client_id")
    )
    @Column(name = "domain")
    private Set<String> allowedDomains;

    public boolean isActive() {
        return this.status == AuthClientStatus.ACTIVE;
    }

    public boolean isSuspended() {
        return this.status == AuthClientStatus.SUSPENDED;
    }
}
