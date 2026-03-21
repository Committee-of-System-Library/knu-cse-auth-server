package kr.ac.knu.cse.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import kr.ac.knu.cse.domain.application.ClientApplication;
import kr.ac.knu.cse.domain.application.ClientApplicationRepository;
import kr.ac.knu.cse.global.exception.auth.AdminAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeveloperAppService {

    private final ClientApplicationRepository clientApplicationRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ClientApplication register(
            Long ownerId,
            String appName,
            String description,
            List<String> redirectUris,
            String homepageUrl
    ) {
        String redirectUrisJson = toJson(redirectUris);

        ClientApplication app = ClientApplication.of(
                appName, description, redirectUrisJson, homepageUrl, ownerId
        );

        return clientApplicationRepository.save(app);
    }

    @Transactional(readOnly = true)
    public List<ClientApplication> findMyApps(Long ownerId) {
        return clientApplicationRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
    }

    @Transactional(readOnly = true)
    public ClientApplication findById(Long id, Long ownerId) {
        ClientApplication app = clientApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("클라이언트를 찾을 수 없습니다."));

        if (!app.isOwner(ownerId)) {
            throw new AdminAccessDeniedException();
        }

        return app;
    }

    @Transactional
    public ClientApplication update(
            Long id,
            Long ownerId,
            String appName,
            String description,
            List<String> redirectUris,
            String homepageUrl
    ) {
        ClientApplication app = findById(id, ownerId);
        app.updateInfo(appName, description, toJson(redirectUris), homepageUrl);
        return app;
    }

    @Transactional
    public void delete(Long id, Long ownerId) {
        ClientApplication app = findById(id, ownerId);
        clientApplicationRepository.delete(app);
    }

    @Transactional
    public String regenerateSecret(Long id, Long ownerId) {
        ClientApplication app = findById(id, ownerId);

        if (!app.isApproved()) {
            throw new IllegalStateException("승인된 클라이언트만 Secret을 재생성할 수 있습니다.");
        }

        String newSecret = UUID.randomUUID().toString();
        app.updateSecret(hashSecret(newSecret));
        return newSecret;
    }

    private String toJson(List<String> redirectUris) {
        try {
            return objectMapper.writeValueAsString(redirectUris);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("redirect_uris 변환 실패", e);
        }
    }

    static String hashSecret(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secret.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
