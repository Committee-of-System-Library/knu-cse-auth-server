package kr.ac.knu.cse.domain.role;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "role_change_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleChangeLog{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "role_change_log_id")
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "before_role", nullable = false, length = 20)
    private Role beforeRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "after_role", nullable = false, length = 20)
    private Role afterRole;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public static RoleChangeLog of(
            Long studentId,
            Role beforeRole,
            Role afterRole,
            LocalDateTime changedAt
    ) {
        return new RoleChangeLog(
                null,
                studentId,
                beforeRole,
                afterRole,
                changedAt,
                null
        );
    }

    public boolean isProcessed() {
        return processedAt != null;
    }

    public void markProcessed() {
        this.processedAt = LocalDateTime.now();
    }
}
