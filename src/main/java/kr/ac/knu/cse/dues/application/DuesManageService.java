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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DuesManageService {

	private final DuesRepository duesRepository;
	private final StudentRepository studentRepository;

	@Transactional
	public Long createDues(CreateDuesDto dto) {
		Student student = studentRepository.findById(dto.studentId())
			.orElseThrow(StudentNotFoundException::new);
		Dues dues = Dues.builder()
			.student(student)
			.depositorName(dto.depositorName())
			.amount(dto.amount())
			.remainingSemesters(dto.remainingSemesters())
			.submittedAt(dto.submittedAt())
			.build();
		duesRepository.save(dues);
		return dues.getId();
	}

	@Transactional
	public void updateDues(Long id, UpdateDuesDto dto) {
		Dues dues = duesRepository.findById(id)
			.orElseThrow(DuesNotFoundException::new);
		dues.updateDuesInfo(dto.depositorName(), dto.amount(), dto.remainingSemesters());
		duesRepository.save(dues);
	}

	@Transactional
	public void deleteDues(Long id) {
		if (!duesRepository.existsById(id)) {
			throw new DuesNotFoundException();
		}
		duesRepository.deleteById(id);
	}

	@Transactional
	public void deleteDues(List<Long> ids) {
		duesRepository.deleteAllById(ids);
	}
}
