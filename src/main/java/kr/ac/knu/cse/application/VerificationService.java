package kr.ac.knu.cse.application;

import java.util.List;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.student.StudentRepository;
import kr.ac.knu.cse.domain.student.UserType;
import kr.ac.knu.cse.domain.verification.VerificationRequest;
import kr.ac.knu.cse.domain.verification.VerificationRequestRepository;
import kr.ac.knu.cse.domain.verification.VerificationStatus;
import kr.ac.knu.cse.global.exception.provisioning.StudentNotFoundException;
import kr.ac.knu.cse.global.exception.verification.VerificationAlreadyPendingException;
import kr.ac.knu.cse.global.exception.verification.VerificationNotNeededException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRequestRepository verificationRequestRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public VerificationRequest submitRequest(
            Long studentId,
            String studentNumber,
            String evidence
    ) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(StudentNotFoundException::new);

        if (student.getUserType() == UserType.CSE_STUDENT) {
            throw new VerificationNotNeededException();
        }

        if (verificationRequestRepository.existsByStudentIdAndStatus(
                studentId, VerificationStatus.PENDING)) {
            throw new VerificationAlreadyPendingException();
        }

        VerificationRequest request = VerificationRequest.of(
                studentId,
                studentNumber,
                evidence
        );

        return verificationRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<VerificationRequest> getMyRequests(Long studentId) {
        return verificationRequestRepository
                .findByStudentIdOrderByCreatedAtDesc(studentId);
    }
}
