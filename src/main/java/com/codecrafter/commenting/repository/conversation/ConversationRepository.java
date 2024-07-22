package com.codecrafter.commenting.repository.conversation;

import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.response.conversation.ConversationDetailsResponse;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codecrafter.commenting.domain.entity.Conversation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author jiheon
 * Conversation 관리 Repository
 */
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query(value = "SELECT cd.*, mi.avatar_path AS avatarPath " +
                    "FROM (" +
                    "    SELECT a.id AS mstId, " +
                    "           a.guest_id AS guestId, " +
                    "           a.owner_id AS ownerId, " +
                    "           b.id AS conId, " +
                    "           b.content, " +
                    "           CASE "+
                    "           WHEN r.recommend_count > 0 THEN true "+
                    "           ELSE false "+
                    "           END AS isGood, "+
                    "           b.is_private AS isPrivate, " +
                    "           b.is_question AS isQuestion, " +
                    "           b.modified_at AS modifiedAt " +
                    "    FROM conversation_mst a " +
                    "    JOIN conversation b ON a.id = b.mst_id " +
                    "    LEFT JOIN ( " +
                    "        SELECT conversation_id, COUNT(*) AS recommend_count " +
                    "        FROM recommend " +
                    "          WHERE recommend_status = 'LIKES' " +
                    "        GROUP BY conversation_id " +
                    "    ) r ON b.id = r.conversation_id " +
                    "    WHERE b.mst_id = :mstId " +
                    ") cd " +
                    "JOIN member_info mi " +
                    "ON (cd.isQuestion = TRUE AND cd.ownerId = mi.id) " +
                    "OR (cd.isQuestion = FALSE AND cd.guestId = mi.id) " +
                    "WHERE cd.mstId = :mstId " +
                    "ORDER BY cd.mstId, cd.conId",
                    nativeQuery = true)
    List<ConversationDetailsResponse> findConversationDetailsByMstId(@Param("mstId") Long mstId);

    @Query(value = "SELECT cd.*, mi.avatar_path AS avatarPath " +
                    "FROM (" +
                    "    SELECT a.id AS mstId, " +
                    "           a.guest_id AS guestId, " +
                    "           a.owner_id AS ownerId, " +
                    "           b.id AS conId, " +
                    "           b.content, " +
                    "           CASE "+
                    "           WHEN r.recommend_count > 0 THEN true "+
                    "           ELSE false "+
                    "           END AS isGood, "+
                    "           b.is_private AS isPrivate, " +
                    "           b.is_question AS isQuestion, " +
                    "           b.modified_at AS modifiedAt " +
                    "    FROM conversation_mst a " +
                    "    JOIN conversation b ON a.id = b.mst_id " +
                    "    LEFT JOIN ( " +
                    "        SELECT conversation_id, COUNT(*) AS recommend_count " +
                    "        FROM recommend " +
                    "          WHERE recommend_status = 'LIKES' " +
                    "        GROUP BY conversation_id " +
                    "    ) r ON b.id = r.conversation_id " +
                    "    WHERE a.owner_id = :ownerId " +
                    ") cd " +
                    "JOIN member_info mi " +
                    "ON (cd.isQuestion = TRUE AND cd.ownerId = mi.id) " +
                    "OR (cd.isQuestion = FALSE AND cd.guestId = mi.id) " +
                    "WHERE cd.ownerId = :ownerId " +
                    "ORDER BY cd.mstId, cd.conId",
                    nativeQuery = true)
    List<ConversationDetailsResponse> findConversationByOwnerId(@Param("ownerId") Long ownerId);


    @Query(value = "SELECT cd.*, mi.avatar_path AS avatarPath " +
                    "FROM (" +
                    "    SELECT a.id AS mstId, " +
                    "           a.guest_id AS guestId, " +
                    "           a.owner_id AS ownerId, " +
                    "           b.id AS conId, " +
                    "           b.content, " +
                    "           CASE "+
                    "           WHEN r.recommend_count > 0 THEN true "+
                    "           ELSE false "+
                    "           END AS isGood, "+
                    "           b.is_private AS isPrivate, " +
                    "           b.is_question AS isQuestion, " +
                    "           b.modified_at AS modifiedAt " +
                    "    FROM conversation_mst a " +
                    "    JOIN conversation b ON a.id = b.mst_id " +
                    "    LEFT JOIN ( " +
                    "        SELECT conversation_id, COUNT(*) AS recommend_count " +
                    "        FROM recommend " +
                    "          WHERE recommend_status = 'LIKES' " +
                    "        GROUP BY conversation_id " +
                    "    ) r ON b.id = r.conversation_id " +
                    "    WHERE a.owner_id = :ownerId AND a.id IN ( " +
                    "         SELECT id FROM conversation_mst " +
                    "         WHERE owner_id = :ownerId " +
                    "         ORDER BY id " +
                    "         LIMIT :pageSize OFFSET :offset " +
                    "    )" +
                    ") cd " +
                    "JOIN member_info mi " +
                    "ON (cd.isQuestion = TRUE AND cd.ownerId = mi.id) " +
                    "OR (cd.isQuestion = FALSE AND cd.guestId = mi.id) " +
                    "WHERE cd.ownerId = :ownerId " +
                    "ORDER BY cd.mstId, cd.conId",
                    nativeQuery = true)
    List<ConversationDetailsResponse> findConversationByOwnerIdPaging(@Param("ownerId") Long ownerId, @Param("pageSize") int pageSize, @Param("offset") int offset);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM conversation WHERE mst_id = :mstId", nativeQuery = true)
    void deleteByConversationMSTId(@Param("mstId") Long mstId);

}
