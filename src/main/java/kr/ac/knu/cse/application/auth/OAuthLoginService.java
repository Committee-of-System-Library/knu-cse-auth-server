package kr.ac.knu.cse.application.auth;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import kr.ac.knu.cse.application.auth.dto.OAuthLoginResult;
import kr.ac.knu.cse.application.auth.dto.OAuthUserInfo;
import kr.ac.knu.cse.domain.provider.Provider;
import kr.ac.knu.cse.domain.provider.ProviderRepository;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthLoginService {
    private static final int MAX_RETRY = 3;
    private static final String TEMP_PREFIX = "TEMP";
    private static final int TEMP_NUMBER_LENGTH = 11;

    private final ProviderRepository providerRepository;
    private final StudentRepository studentRepository;

    public OAuthLoginResult login(OAuthUserInfo userInfo) {
        Provider provider = findProvider(userInfo)
                .orElseGet(() -> createNewUser(userInfo));

        Student student = findStudentOf(provider);

        return new OAuthLoginResult(student.getId());
    }

    private Optional<Provider> findProvider(OAuthUserInfo userInfo) {
        return providerRepository
                .findByProviderNameAndProviderKey(
                        userInfo.getProviderName(),
                        userInfo.getProviderKey()
                );
    }

    private Provider createNewUser(OAuthUserInfo userInfo) {
        Student student = createStudentWithTempNumber(userInfo.getName());

        Provider provider = Provider.of(
                userInfo.getEmail(),
                userInfo.getProviderName(),
                userInfo.getProviderKey(),
                student.getId()
        );

        System.out.println(provider.getStudentId());

        providerRepository.save(provider);

        return provider;
    }

    private Student createStudentWithTempNumber(String name) {
        for (int i = 0; i < MAX_RETRY; i++) {
            try {
                Student student = Student.of(name, generateTempStudentNumber());
                studentRepository.save(student);
                System.out.println(student.getStudentNumber());
                return student;
            } catch (DataIntegrityViolationException e) {
                if (i == MAX_RETRY - 1) {
                    throw new IllegalStateException(
                            "Failed to generate unique temporary student number", e
                    );
                }
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    public String generateTempStudentNumber() {
        // Picks a random value between 0 and 10^11.
        long randomNumber = ThreadLocalRandom.current().nextLong(
                0,
                (long) Math.pow(10, TEMP_NUMBER_LENGTH)
        );

        return TEMP_PREFIX + String.format("%011d", randomNumber);
    }

    private Student findStudentOf(Provider provider) {
        Long studentId = provider.getStudentId();

        if (provider.getStudentId() == null) {
            throw new IllegalStateException("provider doesn't have a student");
        }

        return studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Provider exists but Student not found. providerId=" + provider.getId()
                ));
    }
}
