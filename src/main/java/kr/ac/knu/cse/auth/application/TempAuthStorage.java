package kr.ac.knu.cse.auth.application;

import kr.ac.knu.cse.security.details.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TempAuthStorage {
	private final ConcurrentHashMap<String, TempAuthData> storage = new ConcurrentHashMap<>();

    public TempAuthStorage() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::cleanupExpiredTokens, 1, 1, TimeUnit.MINUTES);
	}

	public String storeAuthData(PrincipalDetails principalDetails, String redirectUrl) {
		String token = UUID.randomUUID().toString();
		TempAuthData authData = new TempAuthData(principalDetails, redirectUrl, LocalDateTime.now().plusMinutes(5));
		storage.put(token, authData);

		log.info("임시 인증 토큰 저장: {} (만료: {})", token, authData.expiresAt());
		return token;
	}

	public String storePrincipalDetails(PrincipalDetails principalDetails) {
		String token = UUID.randomUUID().toString();
		TempAuthData authData = new TempAuthData(principalDetails, null, LocalDateTime.now().plusMinutes(5));
		storage.put(token, authData);

		log.info("임시 인증 토큰 저장 (PrincipalDetails만): {} (만료: {})", token, authData.expiresAt());
		return token;
	}

	public TempAuthData retrieveAndRemove(String token) {
		TempAuthData authData = storage.remove(token);
		if (authData == null) {
			log.warn("존재하지 않는 임시 토큰: {}", token);
			return null;
		}

		if (authData.isExpired()) {
			log.warn("만료된 임시 토큰: {} (만료 시간: {})", token, authData.expiresAt());
			return null;
		}

		log.info("임시 인증 토큰 사용: {}", token);
		return authData;
	}

	private void cleanupExpiredTokens() {
		LocalDateTime now = LocalDateTime.now();

		storage.entrySet().removeIf(entry -> {
			if (entry.getValue().expiresAt().isBefore(now)) {
				log.debug("만료된 토큰 제거: {}", entry.getKey());
				return true;
			}
			return false;
		});
    }

    public record TempAuthData(PrincipalDetails principalDetails, String redirectUrl, LocalDateTime expiresAt) {
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }
}
