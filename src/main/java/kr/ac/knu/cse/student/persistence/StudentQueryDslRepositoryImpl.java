package kr.ac.knu.cse.student.persistence;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import kr.ac.knu.cse.global.support.QueryDslSupport;
import kr.ac.knu.cse.student.presentation.dto.StudentResponse;
import kr.ac.knu.cse.student.presentation.dto.StudentSearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.core.types.Order.ASC;
import static kr.ac.knu.cse.student.domain.QStudent.student;

@Repository
@RequiredArgsConstructor
public class StudentQueryDslRepositoryImpl extends QueryDslSupport implements StudentQueryDslRepository {

    private List<Tuple> fetchContent(Pageable pageable) {
       return queryFactory.select(
                student.studentNumber,
                student.name,
                student.major,
                student.role
        )
        .from(student)
        .orderBy(new OrderSpecifier<>(ASC, student.studentNumber))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
    }

    @Override
    public Page<StudentResponse> findStudents(StudentSearchFilter filter, Pageable pageable) {
        return paginate(
                pageable,
                fetchContent(pageable).stream()
                        .map(tuple -> StudentResponse.of(
                                tuple.get(student.studentNumber),
                                tuple.get(student.name),
                                tuple.get(student.major),
                                tuple.get(student.role)
                        )).toList()
                ,
                countQuery -> countQuery.select(student.count()).from(student)
        );
    }
}
