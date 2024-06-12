package com.codecrafter.commenting.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codecrafter.commenting.domain.entity.ConversationMST;

/**
 * @author jiheon
 * ConversationMST 관리 Repository
 */
public interface ConversationMSTRepository extends JpaRepository<ConversationMST, Long> {

	@EntityGraph(attributePaths = {"conversations"})
	@Query("SELECT m FROM ConversationMST m WHERE m.owner.id = :ownerId AND m.id < :lastSeenId ORDER BY m.id DESC")
	List<ConversationMST> findNextConversations(@Param("ownerId") Long ownerId, @Param("lastSeenId") Long lastSeenId,
		Pageable pageable);

}
