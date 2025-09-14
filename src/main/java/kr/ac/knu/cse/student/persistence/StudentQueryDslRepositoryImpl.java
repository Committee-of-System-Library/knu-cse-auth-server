package kr.ac.knu.cse.student.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import kr.ac.knu.cse.global.support.QueryDslSupport;
import kr.ac.knu.cse.student.domain.Major;
import kr.ac.knu.cse.student.domain.Role;
import kr.ac.knu.cse.student.presentation.dto.StudentResponse;
import kr.ac.knu.cse.student.presentation.dto.StudentSearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;
import static kr.ac.knu.cse.dues.domain.QDues.dues;
import static kr.ac.knu.cse.student.domain.QStudent.student;

@Repository
@RequiredArgsConstructor
public class StudentQueryDslRepositoryImpl extends QueryDslSupport implements StudentQueryDslRepository {

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

        Expression<Boolean> hasDuesExpr = JPAExpressions
                .selectOne()
                .from(dues)
                .where(dues.student.eq(student))
                .exists();

        JPAQuery<Tuple> jpaQuery = queryFactory.select(
                        student.id,
                        student.studentNumber,
                        student.name,
                        student.major,
                        student.role,
                        hasDuesExpr
                )
                .from(student)
                .where(whereBuilder)
                .orderBy(new OrderSpecifier<>(order, sortPath))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Tuple> tuples = jpaQuery.fetch();

        List<StudentResponse> content = tuples.stream()
                .map(tuple -> {
                    Long id = tuple.get(student.id);
                    String studentNumber = tuple.get(student.studentNumber);
                    String name = tuple.get(student.name);
                    Major major = tuple.get(student.major);
                    Role role = tuple.get(student.role);
                    Boolean hasDues = tuple.get(hasDuesExpr);

                    return StudentResponse.of(
                            id,
                            studentNumber,
                            name,
                            major,
                            role,
                            hasDues != null && hasDues
                    );
                })
                .toList();

        return paginate(pageable, content, countQuery -> {
            BooleanBuilder wb = new BooleanBuilder(whereBuilder);

            return countQuery
                    .select(student.count())
                    .from(student)
                    .where(wb);
        });
    }
}
