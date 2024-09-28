package com.codecrafter.commenting.repository.conversation;

import com.codecrafter.commenting.domain.response.conversation.ConversationDetailsResponse;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.Tuple;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.codecrafter.commenting.domain.entity.Conversation;
import org.springframework.data.jpa.repository.Query;

/**
 * @author jiheon
 * Conversation 관리 Repository
 */
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    final static String BASE_QUERY = "SELECT cd.*, mi.avatar_path AS avatarPath, mi.nickname AS nickname " +
                        "FROM (" +
                        "    SELECT a.id AS mstId, " +
                        "           a.guest_id AS guestId, " +
                        "           a.owner_id AS ownerId, " +
                        "           b.id AS conId, " +
                        "           b.content, " +
                        "           CASE WHEN (SELECT COUNT(*) FROM recommend WHERE recommend_status = 'LIKES'  AND user_id = :userId AND conversation_id = b.id) > 0 THEN true " +
                        "               ELSE false END AS isGood, " +
                        "           CASE  WHEN (SELECT COUNT(*)  FROM recommend  WHERE recommend_status = 'THANKED'  AND user_id = :userId  AND conversation_id = b.id) > 0 THEN true " +
                        "               ELSE false END AS isThanked, " +
                        "           b.is_private AS isPrivate, " +
                        "           b.is_question AS isQuestion, " +
                        "           b.modified_at AS modifiedAt " +
                        "    FROM conversation_mst a " +
                        "    JOIN conversation b ON a.id = b.mst_id "
                        ;

    final static String IS_QUESTIONER = "JOIN member_info mi " +
                                            "ON (cd.isQuestion = FALSE AND cd.ownerId = mi.id) " +
                                            "OR (cd.isQuestion = TRUE AND cd.guestId = mi.id) "
                                            ;

    @Query(value = BASE_QUERY +
                    "    WHERE b.mst_id = :mstId " +
                    ") cd " +
                    IS_QUESTIONER +
                    "WHERE cd.mstId = :mstId " +
                    "ORDER BY cd.mstId DESC, cd.conId ASC",
                    nativeQuery = true)
    List<ConversationDetailsResponse> findConversationDetailsByMstId(@Param("mstId") Long mstId, @Param("userId") Long userId);

    @Query(value = BASE_QUERY +
                    "    WHERE a.owner_id = :ownerId " +
                    ") cd " +
                    IS_QUESTIONER +
                    "WHERE cd.ownerId = :ownerId " +
                    "ORDER BY cd.mstId DESC, cd.conId ASC",
                    nativeQuery = true)
    List<ConversationDetailsResponse> findConversationByOwnerId(@Param("ownerId") Long ownerId, @Param("userId") Long userId);


    @Query(value = BASE_QUERY +
                    "    WHERE a.owner_id = :ownerId AND a.id IN ( " +
                    "         SELECT id FROM conversation_mst " +
                    "         WHERE owner_id = :ownerId " +
                    "         ORDER BY id " +
                    "         LIMIT :pageSize OFFSET :offset " +
                    "    )" +
                    ") cd " +
                    IS_QUESTIONER +
                    "WHERE cd.ownerId = :ownerId " +
                    "ORDER BY cd.mstId DESC, cd.conId ASC",
                    nativeQuery = true)
    List<ConversationDetailsResponse> findConversationByOwnerIdPaging(@Param("ownerId") Long ownerId, @Param("pageSize") int pageSize, @Param("offset") int offset, @Param("userId") Long userId);

    void deleteByConversationMSTId(Long mstId);

    @Query(value = BASE_QUERY +
                    "    WHERE b.mst_id BETWEEN :startMstId AND :endMstId " +
                    ") cd " +
                    IS_QUESTIONER +
                    "WHERE cd.mstId BETWEEN :startMstId + 1 AND :endMstId " +
                    "ORDER BY cd.mstId DESC, cd.conId ASC",
                    nativeQuery = true)
    List<Tuple> findByConversationAdd(@Param("maxId") Long startMstId, @Param("id") Long endMstId, @Param("userId") Long userId);

    @Query(value = BASE_QUERY +
                    "    WHERE a.guest_id = :guestId " +
                    ") cd " +
                    IS_QUESTIONER +
                    "WHERE cd.guestId = :guestId " +
                    "ORDER BY cd.conId DESC",
                    nativeQuery = true)
    List<Tuple> findByGuestId(@Param("guestId") Long guestId, @Param("userId") Long userId);


    @Query(value = BASE_QUERY +
                "    WHERE b.id = :conId " +
                ") cd " +
                IS_QUESTIONER +
                "WHERE cd.conId = :conId " +
                "ORDER BY cd.mstId DESC, cd.conId ASC",
                nativeQuery = true)
    Tuple findConversationResponseById(@Param("conId") Long conId, @Param("userId") Long userId);

    @Query(
        value = "SELECT COUNT(*) " +
                "FROM conversation c " +
                "JOIN recommend r " +
                "ON r.conversation_id = c.id " +
                "WHERE c.is_deleted = false AND c.writer_info_id = :id AND c.is_question = true AND r.is_deleted = false AND r.recommend_status = 'LIKES'",
        nativeQuery = true)
    long countGoodQuestionByMemberId(Long id);

    long countByMemberInfoIdAndIsQuestionFalse(Long id);

}
