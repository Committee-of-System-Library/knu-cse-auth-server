package kr.ac.knu.cse.student.presentation;

import jakarta.validation.Valid;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.student.application.StudentService;
import kr.ac.knu.cse.student.application.mapper.StudentMapper;
import kr.ac.knu.cse.student.presentation.dto.PatchUpdateStudentReq;
import kr.ac.knu.cse.student.presentation.dto.PostCreateStudentReq;
import kr.ac.knu.cse.student.presentation.dto.StudentResponse;
import kr.ac.knu.cse.student.presentation.dto.StudentSearchFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
@PreAuthorize("hasRole('FINANCE')")
@Slf4j
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<ApiSuccessResult<Page<StudentResponse>>> getStudents(
            @ModelAttribute final StudentSearchFilter filter,
            final Pageable pageable
    ) {
        log.info("학생 목록 조회 요청 - 필터: {}, 페이지: {}", filter, pageable);
        Page<StudentResponse> response = studentService.getStudents(filter, pageable);
        log.info("학생 목록 조회 완료 - 총 {}명", response.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, response));
    }

    @PostMapping
    public ResponseEntity<ApiSuccessResult<?>> createStudent(
            @Valid @RequestBody PostCreateStudentReq requestBody
    ) {
        log.info("학생 생성 요청 - {}", requestBody);
        Long id = studentService.saveStudent(StudentMapper.toSaveStudentDto(requestBody));
        log.info("학생 생성 완료 - ID: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Student created with ID: " + id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiSuccessResult<?>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody PatchUpdateStudentReq requestBody
    ) {
        log.info("학생 정보 수정 요청 - ID: {}, {}", id, requestBody);
        studentService.updateStudent(id, StudentMapper.toUpdateStudentDto(requestBody));
        log.info("학생 정보 수정 완료 - ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, "Student updated"));
    }

    @DeleteMapping
    public ResponseEntity<ApiSuccessResult<?>> deleteStudents(@RequestParam("ids") List<Long> ids) {
        log.info("학생 삭제 요청 - ID 목록: {}", ids);
        studentService.deleteStudents(ids);
        log.info("학생 삭제 완료");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, "선택된 학생들이 삭제되었습니다."));
    }
}
