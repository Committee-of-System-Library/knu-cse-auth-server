package kr.ac.knu.cse.dues.domain;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.ac.knu.cse.global.domain.BaseEntity;
import kr.ac.knu.cse.student.domain.Student;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "dues")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dues extends BaseEntity {

	@Id
	@Column(name = "dues_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(
		name = "student_id",
		nullable = false
	)
	private Student student;

	@NotNull
	@Column(
		name = "depositor_name",
		length = 50,
		nullable = false
	)
	private String depositorName;

	@NotNull
	@Column(
		name = "amount",
		nullable = false
	)
	private Integer amount;

	@NotNull
	@Column(
		name = "remaining_semesters",
		nullable = false
	)
	private Integer remainingSemesters;

	@NotNull
	@Column(
		name = "submitted_at",
		nullable = false
	)
	private LocalDateTime submittedAt;

	@Builder
	public Dues(
		final Student student,
		final String depositorName,
		final Integer amount,
		final Integer remainingSemesters,
		final LocalDateTime submittedAt
	) {
		this.student = student;
		this.depositorName = depositorName;
		this.amount = amount;
		this.remainingSemesters = remainingSemesters;
		this.submittedAt = submittedAt;
	}
}
