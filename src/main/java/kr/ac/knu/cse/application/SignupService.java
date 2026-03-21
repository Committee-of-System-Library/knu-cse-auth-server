package kr.ac.knu.cse.application;

import java.util.UUID;
import kr.ac.knu.cse.application.dto.SignupCommand;
import kr.ac.knu.cse.application.dto.SignupResponse;
import kr.ac.knu.cse.domain.provider.Provider;
import kr.ac.knu.cse.domain.provider.ProviderRepository;
import kr.ac.knu.cse.domain.registry.CseStudentRegistryRepository;
import kr.ac.knu.cse.domain.role.Role;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.student.StudentRepository;
import kr.ac.knu.cse.domain.student.UserType;
import kr.ac.knu.cse.global.exception.auth.AlreadySignedUpException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {

    private static final String KNU_EMAIL_DOMAIN = "@knu.ac.kr";
    private static final String TEMP_STUDENT_NUMBER_PREFIX = "EXT_";

    private final ProviderRepository providerRepository;
    private final StudentRepository studentRepository;
    private final CseStudentRegistryRepository registryRepository;

    @Transactional
    public SignupResponse signup(SignupCommand command) {
        validateProvider(command.providerName(), command.providerKey());

        String studentNumber = resolveStudentNumber(command.studentNumber());
        validateStudentNumber(studentNumber);

        UserType userType = determineUserType(command.email(), studentNumber);
        Role role = (userType == UserType.CSE_STUDENT) ? Role.STUDENT : null;

        Student student = Student.of(
                command.major(),
                command.name(),
                studentNumber,
                command.grade(),
                command.gender(),
                userType,
                role
        );

        try {
            studentRepository.save(student);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadySignedUpException();
        }

        Provider provider = Provider.of(
                command.email(),
                command.providerName(),
                command.providerKey(),
                student.getId()
        );

        try {
            providerRepository.save(provider);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadySignedUpException();
        }

        return new SignupResponse(student.getId());
    }

    private String resolveStudentNumber(String studentNumber) {
        if (studentNumber == null || studentNumber.isBlank()) {
            return TEMP_STUDENT_NUMBER_PREFIX + UUID.randomUUID().toString().substring(0, 8);
        }
        return studentNumber;
    }

    private UserType determineUserType(String email, String studentNumber) {
        if (email != null && email.endsWith(KNU_EMAIL_DOMAIN)) {
            if (!studentNumber.startsWith(TEMP_STUDENT_NUMBER_PREFIX)
                    && registryRepository.existsByStudentNumber(studentNumber)) {
                return UserType.CSE_STUDENT;
            }
        }
        return UserType.EXTERNAL;
    }

    private void validateProvider(String providerName, String providerKey) {
        if (providerRepository.findByProviderNameAndProviderKey(providerName, providerKey).isPresent()) {
            throw new AlreadySignedUpException();
        }
    }

    private void validateStudentNumber(String studentNumber) {
        if (!studentNumber.startsWith(TEMP_STUDENT_NUMBER_PREFIX)
                && studentRepository.existsByStudentNumber(studentNumber)) {
            throw new AlreadySignedUpException();
        }
    }
}
