package kr.ac.knu.cse.application;

import java.util.List;
import java.util.UUID;
import kr.ac.knu.cse.domain.application.ClientApplication;
import kr.ac.knu.cse.domain.application.ClientApplicationRepository;
import kr.ac.knu.cse.domain.application.ClientApplicationStatus;
import kr.ac.knu.cse.presentation.dto.AppApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAppService {

    private final ClientApplicationRepository clientApplicationRepository;

    @Transactional(readOnly = true)
    public List<ClientApplication> findAll() {
        return clientApplicationRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<ClientApplication> findByStatus(ClientApplicationStatus status) {
        return clientApplicationRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    @Transactional(readOnly = true)
    public ClientApplication findById(Long id) {
        return clientApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("클라이언트를 찾을 수 없습니다."));
    }

    @Transactional
    public AppApproveResponse approve(Long id) {
        ClientApplication app = findById(id);

        if (!app.isPending()) {
            throw new IllegalStateException("PENDING 상태의 클라이언트만 승인할 수 있습니다.");
        }

        String clientId = "cse-" + UUID.randomUUID().toString().substring(0, 8);
        String clientSecret = UUID.randomUUID().toString();
        String secretHash = DeveloperAppService.hashSecret(clientSecret);

        app.approve(clientId, secretHash, null);

        return new AppApproveResponse(app.getId(), clientId, clientSecret);
    }

    @Transactional
    public ClientApplication reject(Long id, String reason) {
        ClientApplication app = findById(id);

        if (!app.isPending()) {
            throw new IllegalStateException("PENDING 상태의 클라이언트만 거부할 수 있습니다.");
        }

        app.reject(reason);
        return app;
    }

    @Transactional
    public ClientApplication suspend(Long id) {
        ClientApplication app = findById(id);

        if (!app.isApproved()) {
            throw new IllegalStateException("승인된 클라이언트만 정지할 수 있습니다.");
        }

        app.suspend();
        return app;
    }
}
