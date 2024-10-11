package com.codecrafter.commenting.repository.conversation;

import com.codecrafter.commenting.domain.response.QuestionedUserInterface;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codecrafter.commenting.domain.entity.ConversationMST;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author jiheon
 * ConversationMST 관리 Repository
 */
public interface ConversationMSTRepository extends JpaRepository<ConversationMST,Long> {

    @Query("SELECT COUNT(c) FROM ConversationMST c WHERE c.owner.id = :ownerId")
    long countByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(c) FROM ConversationMST c WHERE c.guest.id = :guestId")
    long countByGuestId(@Param("guestId") Long guestId);

    @Query("SELECT MAX(c.id) FROM ConversationMST c")
    Long findMaxId();

    List<ConversationMST> findByOwnerId(Long id);

    List<ConversationMST> findByGuestId(Long id);

    @Query(
        value = "SELECT COUNT(*) " +
                "FROM (" +
                "SELECT COUNT(mst.id) " +
                "FROM conversation_mst mst " +
                "JOIN conversation c ON c.mst_id = mst.id " +
                "WHERE mst.owner_id = :ownerId AND mst.is_deleted = false AND c.is_deleted = false " +
                "GROUP BY mst.id " +
                "HAVING COUNT(c.id) = 1)",
        nativeQuery = true
    )
    long countUnansweredQuestionByMemberId(Long ownerId);

    @Query(
        value = "SELECT DISTINCT mst.owner_id AS memberId, mi.avatar_path AS avatarPath "
            + "FROM conversation_mst mst "
            + "JOIN member_info mi ON mst.owner_id = mi.id "
            + "WHERE mst.guest_id = :id AND mst.is_deleted = false",
        nativeQuery = true
    )
    List<QuestionedUserInterface> findQuestionedUserByMemberId(Long id);
}
