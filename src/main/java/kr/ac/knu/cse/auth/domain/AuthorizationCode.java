package kr.ac.knu.cse.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.knu.cse.global.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "authorization_codes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorizationCode extends BaseEntity {
	
	@Id
	@Column(name = "code", length = 36)
	private String code;
	
	@Column(name = "email", nullable = false, length = 255)
	private String email;
	
	@Column(name = "redirect_uri", nullable = false, length = 500)
	private String redirectUrl;
	
	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;
	
	@Column(name = "used", nullable = false)
	private boolean used = false;
	
	@Builder
	public AuthorizationCode(String email, String redirectUrl, LocalDateTime expiresAt) {
		this.code = UUID.randomUUID().toString();
		this.email = email;
		this.redirectUrl = redirectUrl;
		this.expiresAt = expiresAt;
	}
	
	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiresAt);
	}
	
	public boolean isUsed() {
		return used;
	}
	
	public void markAsUsed() {
		this.used = true;
	}
	
	public boolean isValid() {
		return !isExpired() && !isUsed();
	}
}
