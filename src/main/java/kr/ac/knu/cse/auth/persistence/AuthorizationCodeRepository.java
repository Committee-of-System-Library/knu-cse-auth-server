package kr.ac.knu.cse.auth.persistence;

import kr.ac.knu.cse.auth.domain.AuthorizationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, String> {

    Optional<AuthorizationCode> findByCodeAndUsedFalse(String code);

    @Modifying
    @Query("DELETE FROM AuthorizationCode ac WHERE ac.expiresAt < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);
}
