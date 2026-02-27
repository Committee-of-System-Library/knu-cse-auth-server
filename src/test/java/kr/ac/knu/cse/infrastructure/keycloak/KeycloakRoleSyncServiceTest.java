package kr.ac.knu.cse.infrastructure.keycloak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.knu.cse.domain.provider.Provider;
import kr.ac.knu.cse.domain.provider.ProviderRepository;
import kr.ac.knu.cse.domain.role.RoleChangeLog;
import kr.ac.knu.cse.domain.role.RoleChangeLogRepository;
import kr.ac.knu.cse.domain.role.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeycloakRoleSyncServiceTest {
    @Mock
    RoleChangeLogRepository roleChangeLogRepository;

    @Mock
    ProviderRepository providerRepository;

    @Mock
    KeycloakAdminClient keycloakAdminClient;

    @InjectMocks
    KeycloakRoleSyncService roleSyncService;

    LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
    }

    @DisplayName("처리할 로그가 없으면 Keycloak은 호출되지 않는다.")
    @Test
    void syncRoles_NoLogs() {
        // given
        given(roleChangeLogRepository
                .findTop100ByProcessedAtIsNullOrderByChangedAtAsc())
                .willReturn(List.of());

        // when
        roleSyncService.syncRoles();

        // then
        verifyNoInteractions(keycloakAdminClient);
    }

    @DisplayName("처리되지 않은 로그가 있으면 Keycloak에 권한 변경 요청을 보낸다.")
    @Test
    void syncRoles_WithLog() {
        // provider
        Provider provider =  Provider.of(
                "test@knu.ac.kr",
                "keycloak",
                "keycloak-subject",
                20L
        );

        given(providerRepository.findByStudentId(20L))
                .willReturn(Optional.of(provider));

        //RoleChangeLog
        RoleChangeLog log =  RoleChangeLog.of(
                20L,
                RoleType.ROLE_USER,
                RoleType.ROLE_ADMIN,
                now.minusMinutes(10)
        );
        given(roleChangeLogRepository
                .findTop100ByProcessedAtIsNullOrderByChangedAtAsc())
                .willReturn(List.of(log));

        // when
        roleSyncService.syncRoles();

        // then
        verify(keycloakAdminClient).updateRoleInKeycloak(
                "keycloak-subject",
                "ROLE_ADMIN"
        );
    }

    @DisplayName("Keycloak 연동 중 예외가 발생해도 로그는 처리 완료되지 않는다.")
    @Test
    void syncRoles_KeycloakFail() {
        // provider
        Provider provider =  Provider.of(
                "test@knu.ac.kr",
                "keycloak",
                "keycloak-subject",
                20L
        );

        given(providerRepository.findByStudentId(20L))
                .willReturn(Optional.of(provider));

        // RoleChangeLog
        RoleChangeLog log =  RoleChangeLog.of(
                20L,
                RoleType.ROLE_USER,
                RoleType.ROLE_ADMIN,
                now.minusMinutes(10)
        );
        given(roleChangeLogRepository
                .findTop100ByProcessedAtIsNullOrderByChangedAtAsc())
                .willReturn(List.of(log));

        // KeycloakAdminClient
        willThrow(new RuntimeException("keycloak error"))
                .given(keycloakAdminClient)
                .updateRoleInKeycloak(anyString(), anyString());

        // when
        roleSyncService.syncRoles();

        // then
        assertThat(log.getProcessedAt()).isNull();
    }

    @DisplayName("Student에 해당하는 Provider가 없으면 Keycloak은 호출되지 않는다.")
    @Test
    void syncRoles_NoProvider() {
        // Provider
        given(providerRepository.findByStudentId(20L))
                .willReturn(Optional.empty());

        // RoleChangeLog
        RoleChangeLog log =  RoleChangeLog.of(
                20L,
                RoleType.ROLE_USER,
                RoleType.ROLE_ADMIN,
                now.minusMinutes(10)
        );
        given(roleChangeLogRepository
                .findTop100ByProcessedAtIsNullOrderByChangedAtAsc())
                .willReturn(List.of(log));
        // when
        roleSyncService.syncRoles();

        // then
        verifyNoInteractions(keycloakAdminClient);
        assertThat(log.getProcessedAt()).isNull();
    }
}
