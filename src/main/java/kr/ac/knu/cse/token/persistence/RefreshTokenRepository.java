package kr.ac.knu.cse.token.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.ac.knu.cse.token.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
	Optional<RefreshToken> findByEmail(String email);
}
