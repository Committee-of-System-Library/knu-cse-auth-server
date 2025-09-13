package kr.ac.knu.cse.provider.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import kr.ac.knu.cse.global.support.QueryDslSupport;
import kr.ac.knu.cse.provider.presentation.dto.ProviderResponse;
import kr.ac.knu.cse.provider.presentation.dto.ProviderSearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;
import static kr.ac.knu.cse.provider.domain.QProvider.provider;

@Repository
@RequiredArgsConstructor
public class ProviderQueryDslRepositoryImpl extends QueryDslSupport implements ProviderQueryDslRepository {

	private List<Tuple> fetchContent(ProviderSearchFilter filter, Pageable pageable) {
		Order order = filter.getDirection().equalsIgnoreCase("desc") ? DESC : ASC;
		ComparableExpressionBase<?> sortPath = getSortPath(filter.getSortBy());

		BooleanBuilder whereBuilder = new BooleanBuilder();
		if (filter.getSearchColumn() != null && !filter.getSearchColumn().isBlank() &&
			filter.getSearchKeyword() != null && !filter.getSearchKeyword().isBlank()) {
			String keyword = filter.getSearchKeyword();
			switch (filter.getSearchColumn()) {
				case "email":
					whereBuilder.and(provider.email.containsIgnoreCase(keyword));
					break;
				case "providerName":
					whereBuilder.and(provider.providerName.containsIgnoreCase(keyword));
					break;
				case "providerKey":
					whereBuilder.and(provider.providerKey.containsIgnoreCase(keyword));
					break;
				default:
					break;
			}
		}

		return queryFactory
			.select(
				provider.id,
				provider.email,
				provider.providerName,
				provider.providerKey,
				provider.student.id
			)
			.from(provider)
			.where(whereBuilder)
			.orderBy(new OrderSpecifier<>(order, sortPath))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	private ComparableExpressionBase<?> getSortPath(String sortBy) {
		return switch (sortBy) {
			case "id" -> provider.id;
			case "email" -> provider.email;
			case "providerName" -> provider.providerName;
			case "providerKey" -> provider.providerKey;
			default -> provider.email;
		};
	}

	@Override
	public Page<ProviderResponse> findProviders(ProviderSearchFilter filter, Pageable pageable) {
		List<Tuple> tuples = fetchContent(filter, pageable);

		List<ProviderResponse> content = tuples.stream()
			.map(tuple -> new ProviderResponse(
				tuple.get(provider.id),
				tuple.get(provider.email),
				tuple.get(provider.providerName),
				tuple.get(provider.providerKey),
				tuple.get(provider.student.id)
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
						case "email":
							whereBuilder.and(provider.email.containsIgnoreCase(keyword));
							break;
						case "providerName":
							whereBuilder.and(provider.providerName.containsIgnoreCase(keyword));
							break;
						case "providerKey":
							whereBuilder.and(provider.providerKey.containsIgnoreCase(keyword));
							break;
						default:
							break;
					}
				}

				return countQuery
					.select(provider.count())
					.from(provider)
					.where(whereBuilder);
			}
		);
	}
}
