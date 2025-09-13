package kr.ac.knu.cse.student.application;

import kr.ac.knu.cse.student.application.dto.SaveStudentDto;
import kr.ac.knu.cse.student.application.dto.UpdateStudentDto;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.exception.StudentNotFoundException;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import kr.ac.knu.cse.student.presentation.dto.StudentResponse;
import kr.ac.knu.cse.student.presentation.dto.StudentSearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
	private final StudentRepository studentRepository;

	public Student getStudentByStudentNumber(String studentNumber) {
		return studentRepository.findByStudentNumber(studentNumber)
			.orElseThrow(StudentNotFoundException::new);
	}

	public Page<StudentResponse> getStudents(StudentSearchFilter filter, Pageable pageable) {
		return studentRepository.findStudents(filter, pageable);
	}

	@Transactional
	public Long saveStudent(SaveStudentDto dto) {
		Student student = dto.of();
		studentRepository.save(student);
		return student.getId();
	}

	@Transactional
	public void updateStudent(Long id, UpdateStudentDto dto) {
		Student student = studentRepository.findById(id)
			.orElseThrow(StudentNotFoundException::new);
		student.updateStudentInfo(dto.studentNumber(), dto.name(), dto.major(), dto.role());
		studentRepository.save(student);
	}

	@Transactional
	public void deleteStudent(Long id) {
		if (!studentRepository.existsById(id)) {
			throw new StudentNotFoundException();
		}
		studentRepository.deleteById(id);
	}

	@Transactional
	public void deleteStudents(List<Long> ids) {
		studentRepository.deleteAllById(ids);
	}
}
