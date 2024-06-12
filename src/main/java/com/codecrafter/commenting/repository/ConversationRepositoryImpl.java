package com.codecrafter.commenting.repository;

import static com.codecrafter.commenting.domain.entity.QConversationMST.*;

import java.util.List;

import com.codecrafter.commenting.domain.entity.ConversationMST;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * QueryDsl 구현체
 */
@RequiredArgsConstructor
public class ConversationRepositoryImpl implements ConversationRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public List<ConversationMST> findNextConversations(long ownerId, long lastSeenId, int limit) {
		return queryFactory
			.select(conversationMST)
			.from(conversationMST)
			.where(conversationMST.owner.id.eq(ownerId)
				.and(lastSeenIdIsLessThan(lastSeenId)))
			.orderBy(conversationMST.id.desc())
			.limit(limit)
			.fetch();

		// Main query using fetch join
	}

	private BooleanExpression lastSeenIdIsLessThan(long lastSeenId) {
		if (lastSeenId != -1) {
			return conversationMST.id.lt(lastSeenId);
		} else {
			return null; // 조건 없음
		}
	}

}
