package com.codecrafter.commenting.repository;

import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.entity.Recommend;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    Optional<Recommend> findByConversationAndMemberInfo(Conversation conversation, MemberInfo memberInfo);
}
