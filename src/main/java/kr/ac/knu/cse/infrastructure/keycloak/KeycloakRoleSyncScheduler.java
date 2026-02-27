package kr.ac.knu.cse.infrastructure.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Profile("!test")
@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakRoleSyncScheduler {

    private final KeycloakRoleSyncService roleSyncService;

    @Scheduled(
            cron = "${app.role-sync.cron}",
            zone = "${app.role-sync.zone:Asia/Seoul}"
    )
    public void sync() {
        try {
            log.info("Roe sync started");
            roleSyncService.syncRoles();
        } catch (Exception e) {
            log.error("Role sync scheduler failed", e);
        }
    }
}
