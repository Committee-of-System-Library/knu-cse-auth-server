package kr.ac.knu.cse.domain.due;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.knu.cse.global.base.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
### Dues

**Mapping Table**: `dues`

| Field | Type | Description |
| --- | --- | --- |
| id | Long | PK, column name `dues_id` |
| studentId | Long | FK, column name `student_id` |
| amount | int | Paid amount |
| remainingSemesters | int | Remaining valid semesters |
| submittedAt | LocalDateTime | Dues payment timestamp |

### Role

- Dues is the **sole source of truth** for determining the `ROLE_MEMBER` role.
- The `ROLE_MEMBER` value itself is not stored in the database.
- `AuthorizationPolicy` calculates `ROLE_MEMBER` based on the Dues status.
*/

@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "dues")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Dues extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "dues_id")
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;
    @Column(name = "student_number", nullable = false, length = 15)
    private String studentNumber;
    @Column(name = "semesters", nullable = false, length = 15)
    private String semesters;
    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;

    public static Dues of(
            Long studentId,
            String studentNumber,
            String semesters
    ) {
        return new Dues(
                null,
                studentId,
                studentNumber,
                semesters,
                false
        );
    }

    public void pay() {
        this.isPaid = true;
    }
}
