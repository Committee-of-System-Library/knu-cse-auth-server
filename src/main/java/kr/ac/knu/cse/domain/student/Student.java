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
import java.time.LocalDateTime;
import kr.ac.knu.cse.domain.role.RoleType;
import kr.ac.knu.cse.global.base.BaseTimeEntity;
import kr.ac.knu.cse.global.exception.provisioning.InvalidRoleException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "student")
@SQLDelete(sql = "UPDATE student SET deleted_at = NOW() WHERE student_id = ?")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Student extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleType role;

    @Column(name = "major")
    private String major;

    @Column(name = "name")
    private String name;

    @Column(name = "student_number")
    private String studentNumber;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static Student of(
            String major,
            String name,
            String studentNumber,
            Grade grade,
            Gender gender
    ) {
        validate(major, name, studentNumber, grade);

        return new Student(
                null,
                ROLE_USER,
                major,
                name,
                studentNumber,
                grade,
                gender != null ? gender : Gender.BLANK,
                null
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
}
