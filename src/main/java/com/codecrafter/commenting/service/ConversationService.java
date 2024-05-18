package com.codecrafter.commenting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecrafter.commenting.repository.ConversationMSTRepository;
import com.codecrafter.commenting.repository.ConversationRepository;

import lombok.RequiredArgsConstructor;

/**
 * 대화 관련 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConversationService {

	private final ConversationRepository conversationRepository;

	private final ConversationMSTRepository mstRepository;

}
