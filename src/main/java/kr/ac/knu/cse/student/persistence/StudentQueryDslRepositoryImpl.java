package kr.ac.knu.cse.student.persistence;

import static com.querydsl.core.types.Order.*;
import static kr.ac.knu.cse.student.domain.QStudent.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
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

		BooleanBuilder whereBuilder = new BooleanBuilder();
		if (filter.getSearchColumn() != null && !filter.getSearchColumn().isBlank() &&
			filter.getSearchKeyword() != null && !filter.getSearchKeyword().isBlank()) {
			String keyword = filter.getSearchKeyword();

			switch (filter.getSearchColumn()) {
				case "studentNumber":
					whereBuilder.and(student.studentNumber.containsIgnoreCase(keyword));
					break;
				case "name":
					whereBuilder.and(student.name.containsIgnoreCase(keyword));
					break;
				case "major":
					whereBuilder.and(student.major.stringValue().containsIgnoreCase(keyword));
					break;
				case "role":
					whereBuilder.and(student.role.stringValue().containsIgnoreCase(keyword));
					break;
				default:
					break;
			}
		}

		return queryFactory.select(
				student.id,
				student.studentNumber,
				student.name,
				student.major,
				student.role
			)
			.from(student)
			.where(whereBuilder)
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
		List<Tuple> tuples = fetchContent(filter, pageable);

		List<StudentResponse> content = tuples.stream()
			.map(tuple -> StudentResponse.of(
				tuple.get(student.id),
				tuple.get(student.studentNumber),
				tuple.get(student.name),
				tuple.get(student.major),
				tuple.get(student.role)
			))
			.toList();

		return paginate(
			pageable,
			content,
			countQuery -> {
				BooleanBuilder whereBuilder = new BooleanBuilder();
				if (filter.getSearchColumn() != null && !filter.getSearchColumn().isBlank() &&
					filter.getSearchKeyword() != null && !filter.getSearchKeyword().isBlank()) {
					String keyword = filter.getSearchKeyword();

					switch (filter.getSearchColumn()) {
						case "studentNumber":
							whereBuilder.and(student.studentNumber.containsIgnoreCase(keyword));
							break;
						case "name":
							whereBuilder.and(student.name.containsIgnoreCase(keyword));
							break;
						case "major":
							whereBuilder.and(student.major.stringValue().containsIgnoreCase(keyword));
							break;
						case "role":
							whereBuilder.and(student.role.stringValue().containsIgnoreCase(keyword));
							break;
						default:
							break;
					}
				}

				return countQuery
					.select(student.count())
					.from(student)
					.where(whereBuilder);
			}
		);
	}
}
