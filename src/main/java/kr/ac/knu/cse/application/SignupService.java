package kr.ac.knu.cse.application;

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

    private final ProviderRepository providerRepository;
    private final StudentRepository studentRepository;
    private final CseStudentRegistryRepository registryRepository;

    @Transactional
    public SignupResponse signup(SignupCommand command) {
        validate(
                command.providerName(),
                command.providerKey(),
                command.studentNumber()
        );

        UserType userType = determineUserType(command.email(), command.studentNumber());
        Role role = (userType == UserType.CSE_STUDENT) ? Role.STUDENT : null;

        Student student = Student.of(
                command.major(),
                command.name(),
                command.studentNumber(),
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

    private UserType determineUserType(String email, String studentNumber) {
        if (email != null && email.endsWith(KNU_EMAIL_DOMAIN)) {
            if (registryRepository.existsByStudentNumber(studentNumber)) {
                return UserType.CSE_STUDENT;
            }
        }
        return UserType.EXTERNAL;
    }

    private void validate(
            String providerName,
            String providerKey,
            String studentNumber
    ) {
        if (providerRepository.findByProviderNameAndProviderKey(providerName, providerKey).isPresent()
                || studentRepository.existsByStudentNumber(studentNumber)) {
            throw new AlreadySignedUpException();
        }
    }
}
