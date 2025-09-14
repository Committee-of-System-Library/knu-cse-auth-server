package kr.ac.knu.cse.client.application;

import kr.ac.knu.cse.client.domain.AuthClient;
import kr.ac.knu.cse.client.domain.AuthClientStatus;
import kr.ac.knu.cse.client.exception.AuthClientNotFoundException;
import kr.ac.knu.cse.client.exception.DuplicateClientNameException;
import kr.ac.knu.cse.client.persistence.AuthClientRepository;
import kr.ac.knu.cse.client.presentation.dto.AuthClientDetailResponse;
import kr.ac.knu.cse.client.presentation.dto.AuthClientResponse;
import kr.ac.knu.cse.client.presentation.dto.CreateClientRequest;
import kr.ac.knu.cse.client.presentation.dto.UpdateClientRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthClientService {
	private final AuthClientRepository serviceRepository;

	@Transactional
	public Long createAuthClient(CreateClientRequest request) {
		// 클라이언트명 중복 확인
		if (serviceRepository.existsByClientName(request.getClientName())) {
			throw new DuplicateClientNameException();
		}

		AuthClient service = AuthClient.builder()
            .clientName(request.getClientName())
            .clientDescription(request.getClientDescription())
			.allowedDomains(request.getAllowedDomains())
			.build();

		AuthClient savedAuthClient = serviceRepository.save(service);
		log.info("클라이언트 생성 완료 - AuthClientId: {}, AuthClientName: {}",
			savedAuthClient.getClientId(), savedAuthClient.getClientName());

		return savedAuthClient.getClientId();
	}

	@Transactional
	public void updateAuthClient(Long clientId, UpdateClientRequest request) {
		AuthClient service = serviceRepository.findById(clientId)
			.orElseThrow(AuthClientNotFoundException::new);

		// 클라이언트명 변경 시 중복 확인
		if (!service.getClientName().equals(request.getClientName()) &&
			serviceRepository.existsByClientName(request.getClientName())) {
			throw new DuplicateClientNameException();
		}

		service.updateClientInfo(
			request.getClientName(),
			request.getClientDescription(),
			request.getAllowedDomains()
		);

		log.info("클라이언트 정보 수정 완료 - AuthClientId: {}, AuthClientName: {}",
			service.getClientId(), service.getClientName());
	}

	@Transactional
	public void regenerateSecret(Long clientId) {
		AuthClient service = serviceRepository.findById(clientId)
			.orElseThrow(AuthClientNotFoundException::new);

		service.regenerateSecret();
		log.info("클라이언트 JWT Secret 재생성 완료 - AuthClientId: {}", clientId);
	}

	@Transactional
	public void deactivateAuthClient(Long clientId) {
		AuthClient service = serviceRepository.findById(clientId)
			.orElseThrow(AuthClientNotFoundException::new);

		service.deactivate();
		log.info("클라이언트 비활성화 완료 - AuthClientId: {}", clientId);
	}

	@Transactional
	public void activateAuthClient(Long clientId) {
		AuthClient service = serviceRepository.findById(clientId)
			.orElseThrow(AuthClientNotFoundException::new);

		service.activate();
		log.info("클라이언트 활성화 완료 - AuthClientId: {}", clientId);
	}

	public AuthClientDetailResponse getAuthClientDetail(Long clientId) {
		AuthClient service = serviceRepository.findById(clientId)
			.orElseThrow(AuthClientNotFoundException::new);

		return AuthClientDetailResponse.from(service);
	}

	public List<AuthClientResponse> getAllAuthClients() {
		return serviceRepository.findAll().stream()
			.map(AuthClientResponse::from)
			.collect(Collectors.toList());
	}

	public List<AuthClientResponse> getActiveAuthClients() {
		return serviceRepository.findAllByStatus(AuthClientStatus.ACTIVE).stream()
			.map(AuthClientResponse::from)
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteAuthClient(Long clientId) {
		AuthClient service = serviceRepository.findById(clientId)
			.orElseThrow(AuthClientNotFoundException::new);

		serviceRepository.delete(service);
		log.info("클라이언트 삭제 완료 - AuthClientId: {}", clientId);
	}
}
