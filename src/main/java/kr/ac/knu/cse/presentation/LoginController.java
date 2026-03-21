package kr.ac.knu.cse.presentation;

import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import kr.ac.knu.cse.domain.client.AuthClient;
import kr.ac.knu.cse.domain.client.AuthClientRepository;
import kr.ac.knu.cse.global.exception.auth.ClientSuspendedException;
import kr.ac.knu.cse.global.exception.auth.InvalidClientException;
import kr.ac.knu.cse.global.exception.auth.InvalidRedirectUriException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    public static final String SESSION_REDIRECT_URI = "SSO_REDIRECT_URI";
    public static final String SESSION_STATE = "SSO_STATE";
    public static final String SESSION_CLIENT_ID = "SSO_CLIENT_ID";

    private final AuthClientRepository authClientRepository;

    @GetMapping
    public ResponseEntity<Void> login(
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("state") String state,
            HttpSession session
    ) {
        AuthClient client = authClientRepository.findByClientName(clientId)
                .orElseThrow(InvalidClientException::new);

        if (client.isSuspended()) {
            throw new ClientSuspendedException();
        }

        if (!client.isActive()) {
            throw new InvalidClientException();
        }

        validateRedirectUri(redirectUri, client.getAllowedDomains());

        session.setAttribute(SESSION_CLIENT_ID, clientId);
        session.setAttribute(SESSION_REDIRECT_URI, redirectUri);
        session.setAttribute(SESSION_STATE, state);
        session.setAttribute("SSO_NONCE", UUID.randomUUID().toString());

        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, "oauth2/authorization/keycloak")
                .build();
    }

    private void validateRedirectUri(String redirectUri, Set<String> allowedDomains) {
        if (redirectUri == null || redirectUri.isBlank()) {
            throw new InvalidRedirectUriException();
        }

        if (allowedDomains == null || allowedDomains.isEmpty()) {
            throw new InvalidRedirectUriException();
        }

        try {
            String host = URI.create(redirectUri.trim()).getHost();
            if (host == null || allowedDomains.stream().noneMatch(host::endsWith)) {
                throw new InvalidRedirectUriException();
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidRedirectUriException();
        }
    }
}
