package kr.ac.knu.cse.student.persistence;

import kr.ac.knu.cse.student.presentation.dto.StudentResponse;
import kr.ac.knu.cse.student.presentation.dto.StudentSearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentQueryDslRepository {
    Page<StudentResponse> findStudents(StudentSearchFilter filter, Pageable pageable);
}
