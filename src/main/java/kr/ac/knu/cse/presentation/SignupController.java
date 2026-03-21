package kr.ac.knu.cse.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import kr.ac.knu.cse.application.JwtTokenService;
import kr.ac.knu.cse.application.SignupService;
import kr.ac.knu.cse.application.dto.SignupCommand;
import kr.ac.knu.cse.application.dto.SignupResponse;
import kr.ac.knu.cse.domain.registry.CseStudentRegistryRepository;
import kr.ac.knu.cse.global.exception.auth.InvalidOidcUserException;
import kr.ac.knu.cse.global.exception.auth.MissingEmailClaimException;
import kr.ac.knu.cse.infrastructure.security.support.CookieCreator;
import kr.ac.knu.cse.presentation.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignupController {

    private static final String PROVIDER_NAME = "KEYCLOAK";

    private static final String KNU_EMAIL_DOMAIN = "@knu.ac.kr";

    private final SignupService signupService;
    private final JwtTokenService jwtTokenService;
    private final CseStudentRegistryRepository registryRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final CookieCreator cookieCreator;

    @GetMapping("/verify")
    public ResponseEntity<?> verify(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestParam("studentNumber") String studentNumber
    ) {
        if (oidcUser == null) {
            throw new InvalidOidcUserException();
        }

        String email = extractEmail(oidcUser);
        boolean isCseStudent = registryRepository.existsByStudentNumber(studentNumber);
        boolean isKnuEmail = email.endsWith(KNU_EMAIL_DOMAIN);

        return ResponseEntity.ok(Map.of(
                "isCseStudent", isCseStudent,
                "isKnuEmail", isKnuEmail,
                "email", email
        ));
    }

    @PostMapping
    public ResponseEntity<SignupResponse> signup(
            @AuthenticationPrincipal OidcUser oidcUser,
            @Valid @RequestBody SignupRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        if (oidcUser == null) {
            throw new InvalidOidcUserException();
        }

        String email = extractEmail(oidcUser);
        String subject = extractSubject(oidcUser);
        String fullName = extractFullName(oidcUser);

        SignupCommand command = new SignupCommand(
                PROVIDER_NAME,
                subject,
                email,
                request.studentNumber(),
                fullName,
                request.major(),
                request.grade(),
                request.gender(),
                request.userType()
        );

        Long studentId = signupService.signup(command);

        // 가입 성공 → 바로 로그인 처리 (OAuth 재트리거 불필요)
        setAccessTokenCookie(httpResponse);
        String redirectUrl = buildClientRedirectUrl(httpRequest, studentId, email);

        return ResponseEntity.ok(new SignupResponse(redirectUrl));
    }

    private void setAccessTokenCookie(HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof OAuth2AuthenticationToken token)) {
            return;
        }

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(), token.getName()
        );

        if (client != null && client.getAccessToken() != null) {
            ResponseCookie cookie = cookieCreator.createWithValue(
                    client.getAccessToken().getTokenValue()
            );
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
    }

    private String buildClientRedirectUrl(HttpServletRequest request, Long studentId, String email) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        String redirectUri = (String) session.getAttribute(LoginController.SESSION_REDIRECT_URI);
        String state = (String) session.getAttribute(LoginController.SESSION_STATE);
        String clientId = (String) session.getAttribute(LoginController.SESSION_CLIENT_ID);

        if (redirectUri == null || state == null) {
            return null;
        }

        session.removeAttribute(LoginController.SESSION_REDIRECT_URI);
        session.removeAttribute(LoginController.SESSION_STATE);
        session.removeAttribute(LoginController.SESSION_CLIENT_ID);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("state", state);

        if (clientId != null && studentId != null && email != null) {
            try {
                String token = jwtTokenService.generateToken(studentId, email, clientId);
                builder.queryParam("token", token);
            } catch (Exception ignored) {
                // 내부 클라이언트 등 AuthClient가 없는 경우 토큰 없이 진행
            }
        }

        return builder.build(true).toUriString();
    }

    private String extractEmail(OidcUser oidcUser) {
        String email = oidcUser.getEmail();

        if (email == null || email.isBlank()) {
            throw new MissingEmailClaimException();
        }
        return email;
    }

    private String extractSubject(OidcUser oidcUser) {
        String subject = oidcUser.getSubject();

        if (subject == null || subject.isBlank()) {
            throw new InvalidOidcUserException();
        }

        return subject;
    }

    private String extractFullName(OidcUser oidcUser) {
        String name = oidcUser.getFullName();

        if (name != null && !name.isBlank()) {
            return name;
        }

        String preferredUsername = oidcUser.getPreferredUsername();
        if (preferredUsername == null || preferredUsername.isBlank()) {
            throw new InvalidOidcUserException();
        }

        return preferredUsername;
    }
}
