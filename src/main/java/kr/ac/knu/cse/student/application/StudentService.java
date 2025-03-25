package kr.ac.knu.cse.student.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.exception.StudentNotFoundException;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import kr.ac.knu.cse.student.presentation.dto.StudentResponse;
import kr.ac.knu.cse.student.presentation.dto.StudentSearchFilter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {
	private final StudentRepository studentRepository;

	public Student getStudentByStudentNumber(String studentNumber) {
		return studentRepository.findByStudentNumber(studentNumber)
			.orElseThrow(StudentNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public Page<StudentResponse> getStudents(StudentSearchFilter filter, Pageable pageable) {
		return studentRepository.findStudents(filter, pageable);
	}
}
