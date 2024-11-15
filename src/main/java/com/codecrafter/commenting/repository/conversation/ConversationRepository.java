package com.codecrafter.commenting.repository.conversation;

import com.codecrafter.commenting.domain.response.conversation.ConversationDetailsResponse;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.Tuple;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.codecrafter.commenting.domain.entity.Conversation;
import org.springframework.data.jpa.repository.Query;

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
                        "           b.modified_at AS modifiedAt, " +
                        "           b.writer_info_id AS writerId " +
                        "    FROM conversation_mst a " +
                        "    JOIN conversation b ON a.id = b.mst_id "
                        ;

    final static String IS_QUESTIONER = "JOIN member_info mi " +
                                            "ON (cd.isQuestion = FALSE AND cd.ownerId = mi.id) " +
                                            "OR (cd.isQuestion = TRUE AND cd.guestId = mi.id) "
                                            ;

    @Query(value = BASE_QUERY +
                    "    WHERE b.mst_id = :mstId AND a.is_deleted = false AND b.is_deleted = false " +
                    ") cd " +
                    IS_QUESTIONER +
                    "WHERE cd.mstId = :mstId AND mi.is_deleted = false " +
                    "ORDER BY cd.mstId DESC, cd.conId ASC",
                    nativeQuery = true)
    List<ConversationDetailsResponse> findConversationDetailsByMstId(@Param("mstId") Long mstId, @Param("userId") Long userId);

    @Query(value = BASE_QUERY +
                    "    WHERE a.owner_id = :ownerId AND a.is_deleted = false AND b.is_deleted = false " +
                    ") cd " +
                    IS_QUESTIONER +
                    "WHERE cd.ownerId = :ownerId AND mi.is_deleted = false " +
                    "ORDER BY cd.mstId DESC, cd.conId ASC",
                    nativeQuery = true)
    List<ConversationDetailsResponse> findConversationByOwnerId(@Param("ownerId") Long ownerId, @Param("userId") Long userId);

    @Query(value = BASE_QUERY +
                    "    WHERE a.owner_id = :ownerId AND a.is_deleted = false AND b.is_deleted = false AND a.id IN ( " +
                    "         SELECT id FROM conversation_mst " +
                    "         WHERE owner_id = :ownerId AND is_deleted = false " +
                    "         ORDER BY id DESC " +
                    "         LIMIT :pageSize OFFSET :offset " +
                    "    )" +
                    ") cd " +
                    "LEFT " + IS_QUESTIONER +
                    "WHERE cd.ownerId = :ownerId AND (cd.guestId IS NOT NULL AND mi.is_deleted = false OR cd.guestId IS NULL) " +
                    "ORDER BY cd.mstId DESC, cd.conId ASC",
                    nativeQuery = true)
    List<ConversationDetailsResponse> findConversationByOwnerIdPaging(@Param("ownerId") Long ownerId, @Param("pageSize") int pageSize, @Param("offset") int offset, @Param("userId") Long userId);

    @Query(value = BASE_QUERY +
                   "    WHERE a.guest_id = :guestId AND a.is_deleted = false AND b.is_deleted = false AND a.id IN ( " +
                   "         SELECT id FROM conversation_mst " +
                   "         WHERE guest_id = :guestId AND is_deleted = false " +
                   "         ORDER BY id DESC " +
                   "         LIMIT :pageSize OFFSET :offset " +
                   "    )" +
                   ") cd " +
                   IS_QUESTIONER +
                   "WHERE cd.guestId = :guestId AND mi.is_deleted = false " +
                   "ORDER BY cd.mstId DESC, cd.conId ASC",
        nativeQuery = true)
    List<ConversationDetailsResponse> findConversationByGuestIdPaging(@Param("guestId") Long guestId, @Param("pageSize") int pageSize, @Param("offset") int offset, @Param("userId") Long userId);

    void deleteByConversationMSTId(Long mstId);

    @Query(value = BASE_QUERY +
                    "    WHERE b.mst_id BETWEEN :startMstId + 1 AND :endMstId AND a.is_deleted = false AND b.is_deleted = false AND a.owner_id = :ownerId " +
                    ") cd " +
                   "LEFT " +
                    IS_QUESTIONER +
                    "WHERE cd.guestId IS NULL OR mi.is_deleted = false " +
                    "ORDER BY cd.mstId DESC, cd.conId ASC",
                    nativeQuery = true)
    List<Tuple> findByConversationAdd(@Param("startMstId") Long maxId, @Param("endMstId") Long conId, @Param("userId") Long userId, @Param("ownerId") Long ownerId);

    @Query(value = BASE_QUERY +
                    "    WHERE a.guest_id = :guestId AND a.is_deleted = false AND b.is_deleted = false " +
                    ") cd " +
                    IS_QUESTIONER +
                    "WHERE cd.guestId = :guestId AND mi.is_deleted = false " +
                    "ORDER BY cd.conId DESC",
                    nativeQuery = true)
    List<Tuple> findByGuestId(@Param("guestId") Long guestId, @Param("userId") Long userId);


    @Query(value = BASE_QUERY +
                "    WHERE b.id = :conId AND a.is_deleted = false AND b.is_deleted = false " +
                ") cd " +
                IS_QUESTIONER +
                "WHERE cd.conId = :conId AND mi.is_deleted = false " +
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
