package kr.ac.knu.cse.qr.persistence;

import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;
import static kr.ac.knu.cse.qr.domain.QQrAuthLog.qrAuthLog;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import kr.ac.knu.cse.global.support.QueryDslSupport;
import kr.ac.knu.cse.qr.presentation.dto.QrAuthLogResponse;
import kr.ac.knu.cse.qr.presentation.dto.QrAuthLogSearchFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;

import java.util.List;

@Repository
public class QrAuthLogQueryDslRepositoryImpl extends QueryDslSupport implements QrAuthLogQueryDslRepository {

	@Override
	public Page<QrAuthLogResponse> findQrAuthLogs(QrAuthLogSearchFilter filter, Pageable pageable) {
		Order order = filter.getDirection().equalsIgnoreCase("desc") ? DESC : ASC;
		ComparableExpressionBase<?> sortPath = getSortPath(filter.getSortBy());

		List<Tuple> tuples = queryFactory
			.select(
				qrAuthLog.id,
				qrAuthLog.scanDate,
				qrAuthLog.studentNumber,
				qrAuthLog.studentName,
				qrAuthLog.duesPaid,
				qrAuthLog.scannedBy
			)
			.from(qrAuthLog)
			.orderBy(new OrderSpecifier<>(order, sortPath))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		List<QrAuthLogResponse> content = tuples.stream()
			.map(t -> new QrAuthLogResponse(
				t.get(qrAuthLog.id),
				t.get(qrAuthLog.scanDate),
				t.get(qrAuthLog.studentNumber),
				t.get(qrAuthLog.studentName),
				t.get(qrAuthLog.duesPaid),
				t.get(qrAuthLog.scannedBy)
			))
			.toList();

		// 페이지네이션
		return paginate(
			pageable,
			content,
			countQuery -> countQuery
				.select(qrAuthLog.count())
				.from(qrAuthLog)
		);
	}

	private ComparableExpressionBase<?> getSortPath(String sortBy) {
		return switch (sortBy) {
			case "qrAuthLogId" -> qrAuthLog.id;
			case "scanDate" -> qrAuthLog.scanDate;
			case "studentNumber" -> qrAuthLog.studentNumber;
			case "studentName" -> qrAuthLog.studentName;
			case "duesPaid" -> qrAuthLog.duesPaid;
			case "scannedBy" -> qrAuthLog.scannedBy;
			default -> qrAuthLog.scanDate;
		};
	}
}
