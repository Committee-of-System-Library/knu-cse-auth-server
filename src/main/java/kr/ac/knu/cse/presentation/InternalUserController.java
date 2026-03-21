package kr.ac.knu.cse.presentation;

import java.util.List;
import java.util.Map;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.student.StudentRepository;
import kr.ac.knu.cse.domain.student.UserType;
import kr.ac.knu.cse.global.exception.provisioning.StudentNotFoundException;
import kr.ac.knu.cse.presentation.dto.InternalBatchRequest;
import kr.ac.knu.cse.presentation.dto.InternalUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/users")
public class InternalUserController {

    private final StudentRepository studentRepository;

    @GetMapping("/{studentNumber}")
    public ResponseEntity<InternalUserResponse> findByStudentNumber(
            @PathVariable String studentNumber
    ) {
        Student student = studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(StudentNotFoundException::new);

        return ResponseEntity.ok(InternalUserResponse.from(student));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<InternalUserResponse>> findByStudentNumbers(
            @RequestBody InternalBatchRequest request
    ) {
        List<InternalUserResponse> responses = request.studentNumbers().stream()
                .map(studentRepository::findByStudentNumber)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(InternalUserResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{studentNumber}/is-cse-student")
    public ResponseEntity<Map<String, Boolean>> isCseStudent(
            @PathVariable String studentNumber
    ) {
        boolean isCse = studentRepository.findByStudentNumber(studentNumber)
                .map(s -> s.getUserType() == UserType.CSE_STUDENT)
                .orElse(false);

        return ResponseEntity.ok(Map.of("isCseStudent", isCse));
    }

    @GetMapping("/{studentNumber}/role")
    public ResponseEntity<Map<String, Object>> getRole(
            @PathVariable String studentNumber
    ) {
        Student student = studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(StudentNotFoundException::new);

        return ResponseEntity.ok(Map.of(
                "role", student.getRole() != null ? student.getRole().name() : "",
                "userType", student.getUserType().name()
        ));
    }
}
