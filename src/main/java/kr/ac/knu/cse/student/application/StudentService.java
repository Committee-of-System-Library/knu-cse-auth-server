package kr.ac.knu.cse.student.application;

import org.springframework.stereotype.Service;

import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.exception.StudentNotFoundException;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {
	private final StudentRepository studentRepository;

	public Student getStudentByStudentNumber(String studentNumber) {
		return studentRepository.findByStudentNumber(studentNumber)
			.orElseThrow(StudentNotFoundException::new);
	}
}
