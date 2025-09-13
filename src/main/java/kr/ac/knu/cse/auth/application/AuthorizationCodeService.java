package kr.ac.knu.cse.auth.application;

import kr.ac.knu.cse.auth.domain.AuthorizationCode;
import kr.ac.knu.cse.auth.exception.InvalidAuthorizationCodeException;
import kr.ac.knu.cse.auth.persistence.AuthorizationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthorizationCodeService {
	
	private final AuthorizationCodeRepository authorizationCodeRepository;
	
	private static final int CODE_EXPIRATION_MINUTES = 10;
	
	@Transactional
	public String generateCode(String email, String redirectUrl) {
		LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);
		
		AuthorizationCode authorizationCode = AuthorizationCode.builder()
			.email(email)
			.redirectUrl(redirectUrl)
			.expiresAt(expiresAt)
			.build();
		
		AuthorizationCode savedCode = authorizationCodeRepository.save(authorizationCode);
		log.info("Authorization Code 생성 완료 - Email: {}, Code: {}, 만료시간: {}", 
			email, savedCode.getCode(), expiresAt);
		
		return savedCode.getCode();
	}
	
	@Transactional
	public AuthorizationCode validateAndConsumeCode(String code, String redirectUrl) {
		AuthorizationCode authorizationCode = authorizationCodeRepository.findByCodeAndUsedFalse(code)
			.orElseThrow(() -> {
				log.warn("유효하지 않은 Authorization Code: {}", code);
				return new InvalidAuthorizationCodeException();
			});
		
		if (!authorizationCode.isValid()) {
			log.warn("만료되거나 사용된 Authorization Code: {} (만료: {}, 사용됨: {})", 
				code, authorizationCode.isExpired(), authorizationCode.isUsed());
			throw new InvalidAuthorizationCodeException();
		}
		
		if (!authorizationCode.getRedirectUrl().equals(redirectUrl)) {
			log.warn("Redirect URI 불일치 - 저장된: {}, 요청된: {}", 
				authorizationCode.getRedirectUrl(), redirectUrl);
			throw new InvalidAuthorizationCodeException();
		}
		
		authorizationCode.markAsUsed();
		log.info("Authorization Code 검증 및 사용 처리 완료 - Code: {}, Email: {}", 
			code, authorizationCode.getEmail());
		
		return authorizationCode;
	}
	
	@Scheduled(fixedRate = 3600000) // 1시간마다 실행
	@Transactional
	public void cleanupExpiredCodes() {
		authorizationCodeRepository.deleteExpiredCodes(LocalDateTime.now());
		log.debug("만료된 Authorization Code 정리 완료");
	}
}
