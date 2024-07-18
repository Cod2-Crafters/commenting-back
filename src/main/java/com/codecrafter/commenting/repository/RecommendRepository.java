package com.codecrafter.commenting.repository;

import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.entity.Recommend;
import com.codecrafter.commenting.domain.enumeration.RecommendStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    @Query("SELECT r FROM Recommend r WHERE r.conversation = :conversation AND r.memberInfo = :memberInfo AND r.recommendStatus = :status")
    Optional<Recommend> findRecommend(@Param("conversation") Conversation conversation, @Param("memberInfo") MemberInfo memberInfo, @Param("status") RecommendStatus status);
    @Query("SELECT c FROM Conversation c JOIN Recommend r ON c.id = r.conversation.id WHERE r.recommendStatus = :status AND r.memberInfo.id = :userId")
    List<Conversation> findLikedConversationsByUserId(@Param("userId") Long userId, @Param("status") RecommendStatus status);
}
