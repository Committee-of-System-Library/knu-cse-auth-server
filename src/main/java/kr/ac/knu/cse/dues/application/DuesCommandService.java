package kr.ac.knu.cse.dues.application;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.ac.knu.cse.dues.domain.CsvDuesReader;
import kr.ac.knu.cse.dues.domain.Dues;
import kr.ac.knu.cse.dues.exception.DuesNotFoundException;
import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.student.domain.Student;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DuesCommandService {

	private final CsvDuesReader duesReader;
	private final DuesRepository duesRepository;

	@Transactional
	public void submitAll(final InputStream in) {
		final List<Dues> dues = duesReader.read(in);
		duesRepository.saveAll(dues);
	}

	@Transactional(readOnly = true)
	public Dues getMyDues(Student student) {
		return duesRepository.findByStudent(student)
			.orElseThrow(DuesNotFoundException::new);
	}
}
