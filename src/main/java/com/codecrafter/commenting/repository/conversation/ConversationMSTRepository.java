package com.codecrafter.commenting.repository.conversation;

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

    @Query("SELECT MAX(c.id) FROM ConversationMST c")
    Long findMaxId();

}
