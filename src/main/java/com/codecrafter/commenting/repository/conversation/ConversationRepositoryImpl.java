package com.codecrafter.commenting.repository.conversation;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * QueryDsl 구현체
 */
@RequiredArgsConstructor
public class ConversationRepositoryImpl implements ConversationRepositoryCustom {

	private final JPAQueryFactory queryFactory;
}
