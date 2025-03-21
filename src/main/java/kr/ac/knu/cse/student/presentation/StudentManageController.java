package kr.ac.knu.cse.student.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import kr.ac.knu.cse.student.application.StudentManageService;
import kr.ac.knu.cse.student.application.mapper.StudentMapper;
import kr.ac.knu.cse.student.presentation.dto.PatchUpdateStudentReq;
import kr.ac.knu.cse.student.presentation.dto.PostCreateStudentReq;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manage/students")
@PreAuthorize("hasRole('FINANCE')")
public class StudentManageController {
	private final StudentManageService studentManageService;

	@PostMapping
	public ResponseEntity<ApiSuccessResult<?>> createStudent(
		@Valid @RequestBody PostCreateStudentReq requestBody
	) {
		Long id = studentManageService.saveStudent(StudentMapper.toSaveStudentDto(requestBody));
		return ResponseEntity.status((HttpStatus.CREATED))
			.body(ApiResponse.success(HttpStatus.CREATED, "Student created with ID: " + id));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiSuccessResult<?>> updateStudent(
		@PathVariable Long id,
		@Valid @RequestBody PatchUpdateStudentReq requestBody
	) {
		studentManageService.updateStudent(id, StudentMapper.toUpdateStudentDto(requestBody));
		return ResponseEntity.status((HttpStatus.CREATED))
			.body(ApiResponse.success(HttpStatus.OK, "Student updated"));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiSuccessResult<?>> deleteStudent(@PathVariable Long id) {
		studentManageService.deleteStudent(id);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(HttpStatus.OK, "Student deleted"));
	}
}
