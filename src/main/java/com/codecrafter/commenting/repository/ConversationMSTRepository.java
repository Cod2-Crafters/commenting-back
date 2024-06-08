package com.codecrafter.commenting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codecrafter.commenting.domain.entity.ConversationMST;

/**
 * @author jiheon
 * ConversationMST 관리 Repository
 */
public interface ConversationMSTRepository extends JpaRepository<ConversationMST, Long> {

	@EntityGraph(attributePaths = {"conversations"})
	List<ConversationMST> findByOwnerIdOrderByIdDesc(Long ownerId);

}
