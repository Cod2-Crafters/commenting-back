package com.codecrafter.commenting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codecrafter.commenting.domain.entity.Conversation;

/**
 * @author jiheon
 * Conversation 관리 Repository
 */
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}
