package kr.ac.knu.cse.token.application;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.ac.knu.cse.token.domain.RefreshToken;
import kr.ac.knu.cse.token.exception.TokenInvalidException;
import kr.ac.knu.cse.token.persistence.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional(readOnly = true)
	public void validateRefreshToken(String email, String refreshToken) {
		refreshTokenRepository.findByEmail(email)
			.filter(token -> token.getRefreshToken().equals(refreshToken))
			.orElseThrow(TokenInvalidException::new);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "refreshTokens", key = "#p0", unless = "#result == null")
	public String getRefreshToken(String email) {
		return refreshTokenRepository.findByEmail(email)
			.map(RefreshToken::getRefreshToken)
			.orElse(null);
	}

	@Transactional
	@CachePut(value = "refreshTokens", key = "#p0")
	public void updateRefreshToken(String email, String refreshToken) {
		Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByEmail(email);

		if (optionalRefreshToken.isPresent()) {
			RefreshToken savedRefreshToken = optionalRefreshToken.get();
			savedRefreshToken.updateRefreshToken(refreshToken);
		} else {
			RefreshToken savedRefreshToken = RefreshToken.builder()
				.email(email)
				.refreshToken(refreshToken)
				.build();
			refreshTokenRepository.save(savedRefreshToken);
		}
	}

	@Transactional
	@CacheEvict(value = "refreshTokens", key = "#p0")
	public void deleteRefreshToken(String email) {
		refreshTokenRepository.deleteById(email);
	}
}

