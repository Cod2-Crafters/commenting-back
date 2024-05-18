package com.codecrafter.commenting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codecrafter.commenting.domain.entity.ConversationMST;

/**
 * @author jiheon
 * ConversationMST 관리 Repository
 */
public interface ConversationMSTRepository extends JpaRepository<ConversationMST,Long> {

}
