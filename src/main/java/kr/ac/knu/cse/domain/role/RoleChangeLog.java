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

/*
### RoleChangeLog

### Mapping Table

role_change_log

### Fields

Field Name     | Type           | Description
| --- | --- | --- |
id             | Long           | PK, column name `role_change_log_id`
studentId      | Long           | FK, column name `student_id`
beforeRole     | RoleType       | Role before the change, max 20 characters
afterRole      | RoleType       | Role after the change, max 20 characters
changedAt      | LocalDateTime  | Timestamp when the role change occurred
processedAt    | LocalDateTime  | Timestamp when synchronization with the Keycloak DB was completed

### Roles and Responsibilities

- The relationship with Student is expressed only via the FK id (`student_id`); no direct entity association is defined.
- beforeRole represents the user's role prior to the role change.
- afterRole represents the user's role after the role change.
- processedAt remains null until synchronization with the Keycloak database is completed.

 */
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
    private RoleType beforeRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "after_role", nullable = false, length = 20)
    private RoleType afterRole;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public static RoleChangeLog of(
            Long studentId,
            RoleType beforeRole,
            RoleType afterRole,
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
