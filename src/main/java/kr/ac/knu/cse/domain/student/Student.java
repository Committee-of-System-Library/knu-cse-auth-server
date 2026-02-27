package kr.ac.knu.cse.domain.student;

import static jakarta.persistence.GenerationType.IDENTITY;
import static kr.ac.knu.cse.domain.role.RoleType.ROLE_USER;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.knu.cse.domain.role.RoleType;
import kr.ac.knu.cse.global.base.BaseTimeEntity;
import kr.ac.knu.cse.global.exception.provisioning.InvalidRoleException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
### Student

### Mapping Table

`student`

### Fields

| Field Name | Type | Description |
| --- | --- | --- |
| id | Long | PK, column name `student_id` |
| role | RoleType | Top-level organizational role |
| major | String | Major, max 50 characters |
| name | String | Name, max 50 characters |
| studentNumber | String | Student number, max 15 characters, unique |
| grade | String | grade, max 15 characters |
| createdAt | LocalDateTime | Creation timestamp |
| updatedAt | LocalDateTime | Last modified timestamp |

### Constraints

- `student_number` is unique across the entire system.
- The concept of account status is not included.

### Roles and Responsibilities

- Student stores **only the top-level organizational role**.
- `ROLE_MEMBER` is not stored in Student.
- The relationship with Provider is expressed by Provider holding `student_id`.
*/

@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "student")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Student extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private RoleType role;
    @Column(name = "major", nullable = false, length = 50)
    private String major;
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Column(name = "student_number", nullable = false, length = 15, unique = true)
    private String studentNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false, length = 15)
    private Grade grade;

    public static Student of(
            String major,
            String name,
            String studentNumber,
            Grade grade
    ) {
        validate(major, name, studentNumber, grade);

        return new Student(
                null,
                ROLE_USER,
                major,
                name,
                studentNumber,
                grade
        );
    }

    private static void validate(
            String major,
            String name,
            String studentNumber,
            Grade grade) {

    }

    public void grantRole(RoleType role) {
        if (role == null) {
            throw new InvalidRoleException();
        }

        this.role = role;
    }

    public void setIdForTest(Long id) {
        this.id = id;
    }
}
