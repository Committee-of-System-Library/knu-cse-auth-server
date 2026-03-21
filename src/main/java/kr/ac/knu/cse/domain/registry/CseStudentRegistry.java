package kr.ac.knu.cse.domain.registry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "cse_student_registry")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CseStudentRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_number", nullable = false, unique = true, length = 20)
    private String studentNumber;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "major", length = 100)
    private String major;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "is_manually_added")
    private Boolean isManuallyAdded;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static CseStudentRegistry of(
            String studentNumber,
            String name,
            String major,
            Integer grade,
            boolean isManuallyAdded
    ) {
        return new CseStudentRegistry(
                null,
                studentNumber,
                name,
                major,
                grade,
                isManuallyAdded,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public void update(String name, String major, Integer grade) {
        this.name = name;
        this.major = major;
        this.grade = grade;
        this.updatedAt = LocalDateTime.now();
    }
}
