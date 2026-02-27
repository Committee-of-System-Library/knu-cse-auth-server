package kr.ac.knu.cse.infrastructure.keycloak;

import java.util.List;
import kr.ac.knu.cse.domain.provider.Provider;
import kr.ac.knu.cse.domain.provider.ProviderRepository;
import kr.ac.knu.cse.domain.role.RoleChangeLog;
import kr.ac.knu.cse.domain.role.RoleChangeLogRepository;
import kr.ac.knu.cse.global.exception.provisioning.ProviderWithoutStudentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakRoleSyncService {

    private final RoleChangeLogRepository roleChangeLogRepository;
    private final ProviderRepository providerRepository;
    private final KeycloakAdminClient keycloakAdminClient;

    @Transactional
    public void syncRoles() {
        List<RoleChangeLog> logs = roleChangeLogRepository
                .findTop100ByProcessedAtIsNullOrderByChangedAtAsc();

        logs.forEach(this::syncRole);
    }

    private void syncRole(RoleChangeLog roleChangeLog) {
        try {
            doSync(roleChangeLog);
            roleChangeLog.markProcessed();
        } catch (Exception e) {
            log.warn(
                    "Role sync failed. logId={}, studentId={}, afterRole={}",
                    roleChangeLog.getId(),
                    roleChangeLog.getStudentId(),
                    roleChangeLog.getAfterRole(),
                    e
            );
        }
    }

    private void doSync(RoleChangeLog log) {
        Provider provider = findProviderWith(log.getStudentId());

        String subject = provider.getProviderKey();
        String afterRole = log.getAfterRole().name();

        keycloakAdminClient.updateRoleInKeycloak(
                subject,
                afterRole
        );
    }

    private Provider findProviderWith(Long studentId) {
        return providerRepository.findByStudentId(studentId)
                .orElseThrow(ProviderWithoutStudentException::new);
    }
}
