package kr.ac.knu.cse.token.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import kr.ac.knu.cse.global.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {
	@Id
	@Email
	@Column(
		name = "email",
		nullable = false,
		unique = true
	)
	private String email;

	@Column(
		name = "refresh_token",
		nullable = false
	)
	private String refreshToken;

	@Builder
	public RefreshToken(
		final String email,
		final String refreshToken
	) {
		this.email = email;
		this.refreshToken = refreshToken;
	}
}
