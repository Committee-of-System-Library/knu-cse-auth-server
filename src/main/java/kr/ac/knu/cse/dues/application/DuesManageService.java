package kr.ac.knu.cse.dues.application;

import kr.ac.knu.cse.dues.application.dto.CreateDuesDto;
import kr.ac.knu.cse.dues.application.dto.UpdateDuesDto;
import kr.ac.knu.cse.dues.domain.Dues;
import kr.ac.knu.cse.dues.exception.DuesNotFoundException;
import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.exception.StudentNotFoundException;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DuesManageService {

    private final DuesRepository duesRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public Long createDues(CreateDuesDto dto) {
        log.info("학생회비 납부 정보 생성 요청 - 학생 ID: {}", dto.studentId());
        Student student = studentRepository.findById(dto.studentId())
                .orElseThrow(() -> {
                    log.error("학생을 찾을 수 없습니다: {}", dto.studentId());
                    return new StudentNotFoundException();
                });
        Dues dues = Dues.builder()
                .student(student)
                .depositorName(dto.depositorName())
                .amount(dto.amount())
                .remainingSemesters(dto.remainingSemesters())
                .submittedAt(dto.submittedAt())
                .build();
        duesRepository.save(dues);
        log.info("학생회비 납부 정보 생성 완료 - ID: {}", dues.getId());
        return dues.getId();
    }

    @Transactional
    public void updateDues(Long id, UpdateDuesDto dto) {
        log.info("학생회비 납부 정보 수정 요청 - ID: {}", id);
        Dues dues = duesRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("납부 정보를 찾을 수 없습니다: {}", id);
                    return new DuesNotFoundException();
                });
        dues.updateDuesInfo(dto.depositorName(), dto.amount(), dto.remainingSemesters());
        duesRepository.save(dues);
        log.info("학생회비 납부 정보 수정 완료 - ID: {}", id);
    }

    @Transactional
    public void deleteDues(Long id) {
        log.info("학생회비 납부 정보 삭제 요청 - ID: {}", id);
        if (!duesRepository.existsById(id)) {
            log.error("납부 정보를 찾을 수 없습니다: {}", id);
            throw new DuesNotFoundException();
        }
        duesRepository.deleteById(id);
        log.info("학생회비 납부 정보 삭제 완료 - ID: {}", id);
    }

    @Transactional
    public void deleteDues(List<Long> ids) {
        log.info("학생회비 납부 정보 다중 삭제 요청 - ID 목록: {}", ids);
        duesRepository.deleteAllById(ids);
        log.info("학생회비 납부 정보 다중 삭제 완료");
    }
}
