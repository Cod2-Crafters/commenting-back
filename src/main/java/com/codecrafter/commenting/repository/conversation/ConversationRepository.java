package com.codecrafter.commenting.repository.conversation;

import com.codecrafter.commenting.domain.response.conversation.ConversationDetailsResponse;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codecrafter.commenting.domain.entity.Conversation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author jiheon
 * Conversation 관리 Repository
 */
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query(value = "SELECT a.guest_id AS guestId, a.owner_id AS ownerId, b.content, b.is_good AS isGood, b.is_private AS isPrivate, b.is_question AS isQuestion " +
                    "FROM conversation_mst a JOIN conversation b ON a.id = b.mst_id " +
                    "WHERE b.mst_id = :mstId", nativeQuery = true)
    List<ConversationDetailsResponse> findConversationDetailsByMstId(@Param("mstId") Long mstId);

    @Query(value = "SELECT a.id AS mstId, a.guest_id AS guestId, a.owner_id AS ownerId, b.id AS conId, b.content, b.is_good AS isGood, b.is_private AS isPrivate, b.is_question AS isQuestion, b.modified_at AS modifiedAt " +
                    "FROM conversation_mst a, conversation b " +
                    "WHERE a.owner_id = :ownerId AND a.id = b.mst_id " +
                    "ORDER BY b.mst_id, b.id", nativeQuery = true)
    List<ConversationDetailsResponse> findConversationDetailsByOwnerId(@Param("ownerId") Long ownerId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM conversation WHERE mst_id = :mstId", nativeQuery = true)
    void deleteByConversationMSTId(@Param("mstId") Long mstId);

}
