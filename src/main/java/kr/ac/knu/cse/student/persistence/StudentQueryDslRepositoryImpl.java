package kr.ac.knu.cse.student.persistence;

import static com.querydsl.core.types.Order.*;
import static kr.ac.knu.cse.student.domain.QStudent.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;

import kr.ac.knu.cse.global.support.QueryDslSupport;
import kr.ac.knu.cse.student.presentation.dto.StudentResponse;
import kr.ac.knu.cse.student.presentation.dto.StudentSearchFilter;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StudentQueryDslRepositoryImpl extends QueryDslSupport implements StudentQueryDslRepository {

	private List<Tuple> fetchContent(StudentSearchFilter filter, Pageable pageable) {
		Order order = filter.getDirection().equalsIgnoreCase("desc") ? DESC : ASC;
		ComparableExpressionBase<?> sortPath = getSortPath(filter.getSortBy());

		return queryFactory.select(
				student.id,
                student.studentNumber,
                student.name,
                student.major,
                student.role
        )
        .from(student)
			.orderBy(new OrderSpecifier<>(order, sortPath))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
    }

	private ComparableExpressionBase<?> getSortPath(String sortBy) {
		return switch (sortBy) {
			case "id" -> student.id;
			case "name" -> student.name;
			case "major" -> student.major;
			case "role" -> student.role;
			case "studentNumber" -> student.studentNumber;
			default -> student.studentNumber;
		};
	}

	@Override
    public Page<StudentResponse> findStudents(StudentSearchFilter filter, Pageable pageable) {
        return paginate(
                pageable,
			fetchContent(filter, pageable).stream()
                        .map(tuple -> StudentResponse.of(
							tuple.get(student.id),
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
