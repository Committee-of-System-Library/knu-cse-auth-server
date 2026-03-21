package kr.ac.knu.cse.domain.verification;

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
@Table(name = "verification_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VerificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "requested_student_number", nullable = false, length = 20)
    private String requestedStudentNumber;

    @Column(name = "evidence_description", columnDefinition = "TEXT")
    private String evidenceDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private VerificationStatus status;

    @Column(name = "reviewer_id")
    private Long reviewerId;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    public static VerificationRequest of(
            Long studentId,
            String requestedStudentNumber,
            String evidenceDescription
    ) {
        return new VerificationRequest(
                null,
                studentId,
                requestedStudentNumber,
                evidenceDescription,
                VerificationStatus.PENDING,
                null,
                null,
                LocalDateTime.now(),
                null
        );
    }

    public boolean isPending() {
        return this.status == VerificationStatus.PENDING;
    }
}
