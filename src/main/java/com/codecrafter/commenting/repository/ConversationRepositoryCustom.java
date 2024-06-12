package com.codecrafter.commenting.repository;

import java.util.List;

import com.codecrafter.commenting.domain.entity.ConversationMST;

/**
 * QueryDsl 연결용 interface
 */
public interface ConversationRepositoryCustom {

	List<ConversationMST> findNextConversations(long ownerId, long lastSeenId, int limit);

}
