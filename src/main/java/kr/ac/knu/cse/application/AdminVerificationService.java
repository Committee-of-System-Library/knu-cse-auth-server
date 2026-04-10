package kr.ac.knu.cse.application;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.knu.cse.domain.registry.CseStudentRegistry;
import kr.ac.knu.cse.domain.registry.CseStudentRegistryRepository;
import kr.ac.knu.cse.domain.role.Role;
import kr.ac.knu.cse.domain.role.RoleChangeLog;
import kr.ac.knu.cse.domain.role.RoleChangeLogRepository;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.student.StudentRepository;
import kr.ac.knu.cse.domain.student.UserType;
import kr.ac.knu.cse.domain.verification.VerificationRequest;
import kr.ac.knu.cse.domain.verification.VerificationRequestRepository;
import kr.ac.knu.cse.domain.verification.VerificationStatus;
import kr.ac.knu.cse.global.exception.provisioning.StudentNotFoundException;
import kr.ac.knu.cse.global.exception.verification.VerificationNotPendingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminVerificationService {

    private final VerificationRequestRepository verificationRequestRepository;
    private final StudentRepository studentRepository;
    private final CseStudentRegistryRepository registryRepository;
    private final RoleChangeLogRepository roleChangeLogRepository;

    @Transactional(readOnly = true)
    public List<VerificationRequest> findAll() {
        return verificationRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<VerificationRequest> findByStatus(VerificationStatus status) {
        return verificationRequestRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    @Transactional(readOnly = true)
    public VerificationRequest findById(Long id) {
        return verificationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("인증 요청을 찾을 수 없습니다."));
    }

    @Transactional
    public VerificationRequest approve(Long requestId, Long reviewerId, String comment) {
        VerificationRequest request = findById(requestId);

        if (!request.isPending()) {
            throw new VerificationNotPendingException();
        }

        request.approve(reviewerId, comment);

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(StudentNotFoundException::new);

        Role beforeRole = student.getRole();
        student.changeUserType(UserType.CSE_STUDENT);
        student.grantRole(Role.STUDENT);

        if (beforeRole != null) {
            roleChangeLogRepository.save(
                    RoleChangeLog.of(student.getId(), beforeRole, Role.STUDENT, LocalDateTime.now())
            );
        }

        if (!registryRepository.existsByStudentNumber(request.getRequestedStudentNumber())) {
            registryRepository.save(CseStudentRegistry.of(
                    request.getRequestedStudentNumber(),
                    student.getName(),
                    student.getMajor(),
                    null,
                    null,
                    true
            ));
        }

        return request;
    }

    @Transactional
    public VerificationRequest reject(Long requestId, Long reviewerId, String comment) {
        VerificationRequest request = findById(requestId);

        if (!request.isPending()) {
            throw new VerificationNotPendingException();
        }

        request.reject(reviewerId, comment);

        return request;
    }
}
