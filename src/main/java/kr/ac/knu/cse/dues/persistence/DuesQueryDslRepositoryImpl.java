package kr.ac.knu.cse.dues.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import kr.ac.knu.cse.dues.presentation.dto.DuesListResponse;
import kr.ac.knu.cse.dues.presentation.dto.DuesSearchFilter;
import kr.ac.knu.cse.global.support.QueryDslSupport;
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
public class DuesQueryDslRepositoryImpl extends QueryDslSupport implements DuesQueryDslRepository {

    private List<Tuple> fetchContent(DuesSearchFilter filter, Pageable pageable) {
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
                case "depositorName":
                    whereBuilder.and(dues.depositorName.containsIgnoreCase(keyword));
                    break;
                default:
                    break;
            }
        }

        return queryFactory
                .select(
                        dues.id,
                        student.name,
                        student.studentNumber,
                        dues.depositorName,
                        dues.amount,
                        dues.remainingSemesters,
                        dues.submittedAt
                )
                .from(dues)
                .join(dues.student, student)
                .where(whereBuilder)
                .orderBy(new OrderSpecifier<>(order, sortPath))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private ComparableExpressionBase<?> getSortPath(String sortBy) {
        return switch (sortBy) {
            case "duesId" -> dues.id;
            case "name" -> student.name;
            case "studentNumber" -> student.studentNumber;
            case "depositorName" -> dues.depositorName;
            case "amount" -> dues.amount;
            case "remainingSemesters" -> dues.remainingSemesters;
            case "submittedAt" -> dues.submittedAt;
            default -> dues.id;
        };
    }

    @Override
    public Page<DuesListResponse> findDuesList(DuesSearchFilter filter, Pageable pageable) {
        List<Tuple> tuples = fetchContent(filter, pageable);

        List<DuesListResponse> content = tuples.stream()
                .map(tuple -> new DuesListResponse(
                        tuple.get(dues.id),
                        tuple.get(student.name),
                        tuple.get(student.studentNumber),
                        tuple.get(dues.depositorName),
                        tuple.get(dues.amount),
                        tuple.get(dues.remainingSemesters),
                        tuple.get(dues.submittedAt)
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
                            case "depositorName":
                                whereBuilder.and(dues.depositorName.containsIgnoreCase(keyword));
                                break;
                            default:
                                break;
                        }
                    }

                    return countQuery
                            .select(dues.count())
                            .from(dues)
                            .join(dues.student, student)
                            .where(whereBuilder);
                }
        );
    }
}
