package kr.ac.knu.cse.student.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.student.application.StudentService;
import kr.ac.knu.cse.student.presentation.dto.StudentResponse;
import kr.ac.knu.cse.student.presentation.dto.StudentSearchFilter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manage/students")
public class StudentQueryController {
    private final StudentService studentService;

    @GetMapping
    @PreAuthorize("hasRole('FINANCE')")
    public ResponseEntity<ApiSuccessResult<Page<StudentResponse>>> getStudents(
        @ModelAttribute final StudentSearchFilter filter,
        final Pageable pageable
    ) {
        Page<StudentResponse> response = studentService.getStudents(filter, pageable);
        return ResponseEntity.status((HttpStatus.OK))
            .body(ApiResponse.success(HttpStatus.OK, response));
    }
}
