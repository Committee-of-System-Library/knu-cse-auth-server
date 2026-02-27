package kr.ac.knu.cse.infrastructure.keycloak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDateTime;
import kr.ac.knu.cse.domain.provider.Provider;
import kr.ac.knu.cse.domain.provider.ProviderRepository;
import kr.ac.knu.cse.domain.role.RoleChangeLog;
import kr.ac.knu.cse.domain.role.RoleChangeLogRepository;
import kr.ac.knu.cse.domain.role.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class KeycloakRoleSyncIntegrationTest {

    @Autowired
    private KeycloakRoleSyncService roleSyncService;

    @Autowired
    private RoleChangeLogRepository roleChangeLogRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @MockitoBean
    private KeycloakAdminClient keycloakAdminClient;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
    }

    @DisplayName("처리되지 않은 로그가 있으면 Keycloak 동기화 후 processedAt이 갱신된다.")
    @Test
    void syncRoles_success() {
        // given
        providerRepository.save(
                Provider.of(
                        "test@knu.ac.kr",
                        "keycloak",
                        "keycloak-subject",
                        20L
                )
        );

        RoleChangeLog log = roleChangeLogRepository.save(
                RoleChangeLog.of(
                        20L,
                        RoleType.ROLE_USER,
                        RoleType.ROLE_ADMIN,
                        now.minusMinutes(10)
                )
        );

        willDoNothing()
                .given(keycloakAdminClient)
                .updateRoleInKeycloak(anyString(), anyString());


        // when
        roleSyncService.syncRoles();

        // then
        RoleChangeLog result = roleChangeLogRepository.findById(log.getId())
                .orElseThrow();

        assertThat(result.getProcessedAt()).isNotNull();

        verify(keycloakAdminClient).updateRoleInKeycloak(
                "keycloak-subject",
                "ROLE_ADMIN"
        );
    }

    @DisplayName("Keycloak 연동 중 예외가 발생하면 processedAt은 갱신되지 않는다.")
    @Test
    void syncRoles_keycloakFail() {
        // given
        providerRepository.save(
                Provider.of(
                        "test@knu.ac.kr",
                        "keycloak",
                        "subject-30",
                        30L
                )
        );

        RoleChangeLog log = roleChangeLogRepository.save(
                RoleChangeLog.of(
                        30L,
                        RoleType.ROLE_USER,
                        RoleType.ROLE_ADMIN,
                        now.minusMinutes(5)
                )
        );

        willThrow(new RuntimeException("keycloak error"))
                .given(keycloakAdminClient)
                .updateRoleInKeycloak(anyString(), anyString());

        // when
        roleSyncService.syncRoles();

        // then
        RoleChangeLog result = roleChangeLogRepository.findById(log.getId())
                .orElseThrow();

        assertThat(result.getProcessedAt()).isNull();
    }

    @DisplayName("Student에 해당하는 Provider가 없으면 로그는 처리되지 않는다.")
    @Test
    void syncRoles_noProvider() {
        // given
        RoleChangeLog log = roleChangeLogRepository.save(
                RoleChangeLog.of(
                        40L,
                        RoleType.ROLE_USER,
                        RoleType.ROLE_ADMIN,
                        now.minusMinutes(3)
                )
        );

        // when
        roleSyncService.syncRoles();

        // then
        RoleChangeLog result = roleChangeLogRepository.findById(log.getId())
                .orElseThrow();

        assertThat(result.getProcessedAt()).isNull();
        verifyNoInteractions(keycloakAdminClient);
    }
}

