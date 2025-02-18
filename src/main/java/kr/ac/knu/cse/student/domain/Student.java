package kr.ac.knu.cse.student.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.ac.knu.cse.global.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "student")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Student extends BaseEntity {

	@Id
	@Column(name = "student_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(
		name = "student_number",
		length = 15,
		unique = true,
		nullable = false
	)
	private String studentNumber;

	@NotNull
	@Column(
		name = "name",
		length = 50,
		nullable = false
	)
	private String name;

	@NotNull
	@Column(
		name = "major",
		nullable = false
	)
	@Enumerated(EnumType.STRING)
	private Major major;

	@NotNull
	@Column(
		name = "role",
		nullable = false
	)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder
	public Student(
		final String studentNumber,
		final String name,
		final Major major,
		final Role role
	) {
		this.studentNumber = studentNumber;
		this.name = name;
		this.role = role;
		this.major = major;
	}
}
