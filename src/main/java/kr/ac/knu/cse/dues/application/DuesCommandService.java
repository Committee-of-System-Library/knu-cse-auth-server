package kr.ac.knu.cse.dues.application;

import kr.ac.knu.cse.dues.domain.Dues;
import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.student.domain.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DuesCommandService {

    private final DuesRepository duesRepository;

    @Transactional(readOnly = true)
    public Boolean checkMyDues(Student student) {
        Optional<Dues> optionalDues = duesRepository.findByStudent(student);
        return optionalDues.isPresent();
    }
}
