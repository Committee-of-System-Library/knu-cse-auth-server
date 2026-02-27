package kr.ac.knu.cse.application;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import java.util.Optional;
import kr.ac.knu.cse.application.dto.OAuthLoginResult;
import kr.ac.knu.cse.application.dto.OAuthUserInfo;
import kr.ac.knu.cse.domain.provider.Provider;
import kr.ac.knu.cse.domain.provider.ProviderRepository;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.student.StudentRepository;
import kr.ac.knu.cse.global.exception.provisioning.ProviderWithoutStudentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final ProviderRepository providerRepository;
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true, propagation = REQUIRES_NEW)
    public OAuthLoginResult login(OAuthUserInfo userInfo) {
        Optional<Provider> provider = findProvider(userInfo);

        if (provider.isEmpty()) {
            return OAuthLoginResult.newUser();
        }
        Student student = findStudent(provider.get());

        return OAuthLoginResult.existingUser(student.getId());
    }

    private Optional<Provider> findProvider(OAuthUserInfo userInfo) {
        return providerRepository
                .findByProviderNameAndProviderKey(
                        userInfo.getProviderName(),
                        userInfo.getProviderKey()
                );
    }

    private Student findStudent(Provider provider) {
        return studentRepository.findById(provider.getStudentId())
                .orElseThrow(ProviderWithoutStudentException::new);
    }
}
