package kr.ac.knu.cse.infrastructure.keycloak;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeycloakRoleSyncSchedulerTest {

    @Mock
    KeycloakRoleSyncService roleSyncService;

    @InjectMocks
    KeycloakRoleSyncScheduler scheduler;

    @DisplayName("스케줄러가 실행되면 RoleSyncService를 호출한다.")
    @Test
    void sync_callsService() {
        // given
        willDoNothing()
                .given(roleSyncService)
                .syncRoles();

        // when
        scheduler.sync();

        // then
        verify(roleSyncService).syncRoles();
    }

    @DisplayName("RoleSyncService에서 예외가 발생해도 스케줄러는 예외를 던지지 않는다.")
    @Test
    void sync_exceptionIsCaught() {
        // given
        willThrow(new RuntimeException("sync failed"))
                .given(roleSyncService)
                .syncRoles();

        // when && then
        assertDoesNotThrow(() -> scheduler.sync());

        verify(roleSyncService).syncRoles();
    }
}
