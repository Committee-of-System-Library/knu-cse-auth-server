package kr.ac.knu.cse.domain.snack;

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
@Table(name = "snack_event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SnackEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10)
    private String semester;

    @Column(name = "requires_payment", nullable = false)
    private boolean requiresPayment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SnackEventStatus status;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "opened_by_student_number", length = 20)
    private String openedByStudentNumber;

    public static SnackEvent open(
            String name,
            String semester,
            boolean requiresPayment,
            String openedByStudentNumber
    ) {
        return new SnackEvent(
                null,
                name,
                semester,
                requiresPayment,
                SnackEventStatus.OPEN,
                LocalDateTime.now(),
                null,
                openedByStudentNumber
        );
    }

    public void close() {
        this.status = SnackEventStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    public boolean isOpen() {
        return this.status == SnackEventStatus.OPEN;
    }
}
