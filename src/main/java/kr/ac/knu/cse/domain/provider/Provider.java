package kr.ac.knu.cse.domain.provider;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.ac.knu.cse.global.base.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
### Provider

### Mapping Table

`provider`

### Fields

| Field Name | Type | Description |
| --- | --- | --- |
| id | Long | PK, column name `provider_id` |
| email | String | Provider-based email, unique |
| providerName | String | Authentication provider name (e.g., GOOGLE) |
| providerKey | String | User identifier within the provider |
| studentId | Long | FK, column name `student_id`, nullable |
| createdAt | LocalDateTime | Creation timestamp |
| updatedAt | LocalDateTime | Last modified timestamp |

### Constraints

- `email` is unique.
- `student_id` is nullable.
- When a Student is deleted, Provider.`student_id` is set to NULL.

### Roles and Responsibilities

- Provider represents an **external authentication account (OAuth account)**.
- Provider is **loosely coupled** with Student.
- An authentication account may exist without being linked to an internal user (Student).
- Provider stores **OAuth identification information** (email, providerKey).
*/

@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(
        name = "provider",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "provider_name_key_uq",
                        columnNames = {"provider_name", "provider_key"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Provider extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "provider_id")
    private Long id;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;
    @Column(name = "provider_name", nullable = false, length = 255)
    private String providerName;
    @Column(name = "provider_key", nullable = false, length = 255)
    private String providerKey;
    @Column(name = "student_id")
    private Long studentId;

    public static Provider of(
            String email,
            String providerName,
            String providerKey,
            Long studentId
    ) {
        return new Provider(
                null,
                email,
                providerName,
                providerKey,
                studentId
        );
    }
}
