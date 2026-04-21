package kr.ac.knu.cse.presentation;

import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import kr.ac.knu.cse.application.AdminAuthService;
import kr.ac.knu.cse.application.SnackEventService;
import kr.ac.knu.cse.application.SnackXlsxExporter;
import kr.ac.knu.cse.application.dto.HandoutScanResult;
import kr.ac.knu.cse.application.dto.SnackEventResponse;
import kr.ac.knu.cse.application.dto.SnackHandoutResponse;
import kr.ac.knu.cse.domain.snack.SnackEvent;
import kr.ac.knu.cse.domain.snack.SnackEventStatus;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.presentation.dto.SnackEventCreateRequest;
import kr.ac.knu.cse.presentation.dto.SnackHandoutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/snacks")
public class SnackAdminController {

    private final AdminAuthService adminAuthService;
    private final SnackEventService snackEventService;
    private final SnackXlsxExporter xlsxExporter;

    @GetMapping("/semester-suggestion")
    public ResponseEntity<Map<String, String>> semesterSuggestion(
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(Map.of("semester", snackEventService.suggestSemester()));
    }

    @PostMapping("/events")
    public ResponseEntity<SnackEventResponse> createEvent(
            @AuthenticationPrincipal OidcUser oidcUser,
            @Valid @RequestBody SnackEventCreateRequest request
    ) {
        Student admin = adminAuthService.requireAdmin(oidcUser);
        SnackEventResponse response = snackEventService.openEvent(
                request.name(),
                request.semester(),
                request.requiresPayment(),
                admin.getStudentNumber()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/events")
    public ResponseEntity<List<SnackEventResponse>> listEvents(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestParam(required = false) SnackEventStatus status
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(snackEventService.listEvents(status));
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<SnackEventResponse> getEvent(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long eventId
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(snackEventService.getEvent(eventId));
    }

    @GetMapping("/events/{eventId}/handouts")
    public ResponseEntity<List<SnackHandoutResponse>> listHandouts(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long eventId
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(snackEventService.listHandouts(eventId));
    }

    @PostMapping("/events/{eventId}/handouts")
    public ResponseEntity<HandoutScanResult> handout(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long eventId,
            @Valid @RequestBody SnackHandoutRequest request
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(
                snackEventService.handout(eventId, request.studentNumber())
        );
    }

    @PostMapping("/events/{eventId}/close")
    public ResponseEntity<SnackEventResponse> closeEvent(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long eventId
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(snackEventService.closeEvent(eventId));
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long eventId
    ) {
        adminAuthService.requireAdmin(oidcUser);
        snackEventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events/{eventId}/export.xlsx")
    public ResponseEntity<byte[]> exportXlsx(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long eventId
    ) {
        adminAuthService.requireAdmin(oidcUser);

        SnackEvent event = snackEventService.findEventEntity(eventId);
        byte[] body = xlsxExporter.export(event, snackEventService.findHandoutsForExport(eventId));

        String filename = buildFilename(event.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ));
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"snack.xlsx\"; filename*=UTF-8''" + filename);
        headers.setContentLength(body.length);

        return ResponseEntity.ok().headers(headers).body(body);
    }

    private String buildFilename(String eventName) {
        String safe = eventName.replaceAll("[\\\\/:*?\"<>|]", "_");
        return URLEncoder.encode(safe + ".xlsx", StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}
