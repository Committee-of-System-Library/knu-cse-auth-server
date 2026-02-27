package kr.ac.knu.cse.presentation;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import kr.ac.knu.cse.domain.redirect.RedirectUriPolicy;
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

    private final RedirectUriPolicy redirectUriPolicy;

    @GetMapping
    public ResponseEntity<Void> login(
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("state") String state,
            HttpSession session
    ) {
        redirectUriPolicy.validate(redirectUri);

        session.setAttribute(SESSION_REDIRECT_URI, redirectUri);
        session.setAttribute(SESSION_STATE, state);
        session.setAttribute("SSO_NONCE", UUID.randomUUID().toString());

        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, "/oauth2/authorization/keycloak")
                .build();
    }
}
