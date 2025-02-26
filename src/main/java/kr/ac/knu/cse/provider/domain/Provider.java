package kr.ac.knu.cse.provider.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import kr.ac.knu.cse.global.domain.BaseEntity;
import kr.ac.knu.cse.student.domain.Student;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "provider")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Provider extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "provider_id")
	private Long id;

	@Email
	@NotNull
	@Column(
		name = "email",
		nullable = false,
		unique = true
	)
	private String email;

	@NotNull
	@Column(
		name = "provider",
		nullable = false
	)
	private String provider;

	@NotNull
	@Column(
		name = "provider_id",
		nullable = false
	)
	private String providerId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "student_id",
		nullable = true
	)
	private Student student;

	@Builder
	public Provider(
		final String email,
		final String provider,
		final String providerId
	) {
		this.email = email;
		this.provider = provider;
		this.providerId = providerId;
	}
}
