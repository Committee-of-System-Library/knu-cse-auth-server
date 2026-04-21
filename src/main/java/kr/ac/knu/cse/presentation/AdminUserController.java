package kr.ac.knu.cse.presentation;

import jakarta.validation.Valid;
import java.util.List;
import kr.ac.knu.cse.application.AdminAuthService;
import kr.ac.knu.cse.application.AdminUserService;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.presentation.dto.RoleChangeRequest;
import kr.ac.knu.cse.presentation.dto.UserTypeChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminAuthService adminAuthService;
    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<List<Student>> findAll(
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(adminUserService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> findById(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(adminUserService.findById(id));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<Student> changeRole(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id,
            @Valid @RequestBody RoleChangeRequest request
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(adminUserService.changeRole(id, request.role()));
    }

    @PutMapping("/{id}/user-type")
    public ResponseEntity<Student> changeUserType(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id,
            @Valid @RequestBody UserTypeChangeRequest request
    ) {
        adminAuthService.requireAdmin(oidcUser);
        return ResponseEntity.ok(adminUserService.changeUserType(id, request.userType()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id
    ) {
        adminAuthService.requireAdmin(oidcUser);
        adminUserService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
