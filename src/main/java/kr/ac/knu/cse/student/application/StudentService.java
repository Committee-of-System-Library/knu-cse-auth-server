package kr.ac.knu.cse.student.application;

import kr.ac.knu.cse.student.application.dto.SaveStudentDto;
import kr.ac.knu.cse.student.application.dto.UpdateStudentDto;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.exception.StudentNotFoundException;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import kr.ac.knu.cse.student.presentation.dto.StudentResponse;
import kr.ac.knu.cse.student.presentation.dto.StudentSearchFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StudentService {
    private final StudentRepository studentRepository;

    public Student getStudentByStudentNumber(String studentNumber) {
        log.info("학번으로 학생 조회 - 학번: {}", studentNumber);
        return studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> {
                    log.error("학생을 찾을 수 없습니다: {}", studentNumber);
                    return new StudentNotFoundException();
                });
    }

    public Page<StudentResponse> getStudents(StudentSearchFilter filter, Pageable pageable) {
        log.info("학생 목록 조회 - 필터: {}, 페이지: {}", filter, pageable);
        return studentRepository.findStudents(filter, pageable);
    }

    @Transactional
    public Long saveStudent(SaveStudentDto dto) {
        log.info("학생 정보 저장 - 학번: {}", dto.studentNumber());
        Student student = dto.of();
        studentRepository.save(student);
        log.info("학생 정보 저장 완료 - ID: {}", student.getId());
        return student.getId();
    }

    @Transactional
    public void updateStudent(Long id, UpdateStudentDto dto) {
        log.info("학생 정보 수정 - ID: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("학생을 찾을 수 없습니다: {}", id);
                    return new StudentNotFoundException();
                });
        student.updateStudentInfo(dto.studentNumber(), dto.name(), dto.major(), dto.role());
        studentRepository.save(student);
        log.info("학생 정보 수정 완료 - ID: {}", id);
    }

    @Transactional
    public void deleteStudent(Long id) {
        log.info("학생 정보 삭제 - ID: {}", id);
        if (!studentRepository.existsById(id)) {
            log.error("학생을 찾을 수 없습니다: {}", id);
            throw new StudentNotFoundException();
        }
        studentRepository.deleteById(id);
        log.info("학생 정보 삭제 완료 - ID: {}", id);
    }

    @Transactional
    public void deleteStudents(List<Long> ids) {
        log.info("학생 정보 다중 삭제 - ID 목록: {}", ids);
        studentRepository.deleteAllById(ids);
        log.info("학생 정보 다중 삭제 완료");
    }
}
