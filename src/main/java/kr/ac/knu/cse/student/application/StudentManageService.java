package kr.ac.knu.cse.student.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.ac.knu.cse.student.application.dto.SaveStudentDto;
import kr.ac.knu.cse.student.application.dto.UpdateStudentDto;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.exception.StudentNotFoundException;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentManageService {
	private final StudentRepository studentRepository;

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
}
