package kr.ac.knu.cse.application;

import kr.ac.knu.cse.domain.provider.Provider;
import kr.ac.knu.cse.domain.provider.ProviderRepository;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.student.StudentRepository;
import kr.ac.knu.cse.domain.student.UserType;
import kr.ac.knu.cse.global.exception.auth.AdminAccessDeniedException;
import kr.ac.knu.cse.global.exception.provisioning.ProviderWithoutStudentException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeveloperAuthService {

    private final ProviderRepository providerRepository;
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public Student requireCseStudent(OidcUser oidcUser) {
        String providerKey = oidcUser.getSubject();

        Provider provider = providerRepository
                .findByProviderNameAndProviderKey("KEYCLOAK", providerKey)
                .orElseThrow(AdminAccessDeniedException::new);

        Student student = studentRepository.findById(provider.getStudentId())
                .orElseThrow(ProviderWithoutStudentException::new);

        if (student.getUserType() != UserType.CSE_STUDENT && !student.isAdmin()) {
            throw new AdminAccessDeniedException();
        }

        return student;
    }
}
