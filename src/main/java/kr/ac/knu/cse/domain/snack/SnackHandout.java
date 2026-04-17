package kr.ac.knu.cse.domain.snack;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "snack_handout",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_snack_handout_event_student",
                columnNames = {"event_id", "student_number"}
        ),
        indexes = @Index(name = "idx_snack_handout_event", columnList = "event_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SnackHandout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "student_number", nullable = false, length = 20)
    private String studentNumber;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 100)
    private String major;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    public static SnackHandout of(
            Long eventId,
            String studentNumber,
            String name,
            String major
    ) {
        return new SnackHandout(
                null,
                eventId,
                studentNumber,
                name,
                major,
                LocalDateTime.now()
        );
    }
}
