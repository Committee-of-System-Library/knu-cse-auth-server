package kr.ac.knu.cse.application;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.knu.cse.domain.role.Role;
import kr.ac.knu.cse.domain.role.RoleChangeLog;
import kr.ac.knu.cse.domain.role.RoleChangeLogRepository;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.student.StudentRepository;
import kr.ac.knu.cse.domain.student.UserType;
import kr.ac.knu.cse.global.exception.provisioning.StudentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final StudentRepository studentRepository;
    private final RoleChangeLogRepository roleChangeLogRepository;

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(StudentNotFoundException::new);
    }

    @Transactional
    public Student changeRole(Long studentId, Role newRole) {
        Student student = findById(studentId);
        Role beforeRole = student.getRole();

        student.grantRole(newRole);

        if (beforeRole != null) {
            roleChangeLogRepository.save(
                    RoleChangeLog.of(studentId, beforeRole, newRole, LocalDateTime.now())
            );
        }

        return student;
    }

    @Transactional
    public Student changeUserType(Long studentId, UserType userType) {
        Student student = findById(studentId);
        student.changeUserType(userType);

        if (userType == UserType.CSE_STUDENT && student.getRole() == null) {
            student.grantRole(Role.STUDENT);
        }

        return student;
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = findById(studentId);
        studentRepository.delete(student);
    }
}
