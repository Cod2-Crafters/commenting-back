package com.codecrafter.commenting.service;

import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.ConversationMST;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.request.conversation.CreateConversationRequest;
import com.codecrafter.commenting.domain.request.conversation.UpdateConversationRequest;
import com.codecrafter.commenting.domain.response.conversation.ConversationDetailsResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationResponse;
import com.codecrafter.commenting.repository.MemberInfoRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ConversationService {

	private final ConversationMSTRepository conversationMSTRepository;
	private final ConversationRepository conversationRepository;
	private final MemberInfoRepository memberInfoRepository;

	@Transactional(readOnly = true)
	public ConversationResponse getConversation(Long id) {
		Conversation conversation = findConversationById(id);
		return convertToResponse(conversation);
	}

	@Transactional(readOnly = true)
	public List<ConversationDetailsResponse> getConversationDetails(Long id) {
		return conversationRepository.findConversationDetailsByMstId(id);
	}

	@Transactional(readOnly = true)
	public List<ConversationDetailsResponse> getConversationDetailsByOwnerId(Long id) {
		return conversationRepository.findConversationDetailsByOwnerId(id);
	}

	@Transactional
	public ConversationMST createConversation(CreateConversationRequest request) {
		MemberInfo owner = memberInfoRepository.findById(request.ownerId())
																.orElseThrow(() -> new IllegalArgumentException("존재하지않는 프로필입니다."));
		// 익명의 사용자일 경우 널처리
		MemberInfo guest = request.guestId() != null ? memberInfoRepository.findById(request.guestId()).orElse(null) : null;

		ConversationMST conversationMST = ConversationMST.create(owner, guest);
		conversationMST = conversationMSTRepository.save(conversationMST);

		MemberInfo writerInfo = request.guestId() != null ? guest : null;

		Conversation conversation = Conversation.builder()
												.content(request.content())
												.isPrivate(request.isPrivate())
												.isQuestion(true) // true = 질문, false = 답변
												.memberInfo(writerInfo)
												.build();
		conversation.setConversationMST(conversationMST);

		conversationRepository.save(conversation);

		return conversationMST;
	}

	@Transactional
	public Conversation updateConversation(UpdateConversationRequest request) {
		Conversation conversation = findConversationById(request.conId());

		conversation.setContent(request.content());
		conversation.setPrivate(request.isPrivate());
		conversation.setQuestion(true);

		return conversationRepository.save(conversation);
	}

	@Transactional
	public void deleteConversationAndDetails(Long mstId) {
		conversationRepository.deleteByConversationMSTId(mstId);
		conversationMSTRepository.deleteById(mstId);
	}

	@Transactional
	public ConversationResponse addAnswer(CreateConversationRequest request) {
		ConversationMST conversationMST = conversationMSTRepository.findById(request.mstId())
																	.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대화입니다."));
		MemberInfo writer = memberInfoRepository.findById(request.ownerId())
																	.orElseThrow(() -> new IllegalArgumentException("스페이스 주인을 찾을 수 없습니다."));

		Conversation answer = Conversation.builder()
											.content(request.content())
											.isPrivate(request.isPrivate())
											.isQuestion(false) // true = 질문, false = 답변
											.memberInfo(writer)
											.build();

		answer.setConversationMST(conversationMST);
		conversationRepository.save(answer);

		return convertToResponse(answer);
	}

	public Conversation updateAddAnswer(UpdateConversationRequest request) {
		Conversation conversation = findConversationById(request.conId());

		conversation.setContent(request.content());
		conversation.setPrivate(request.isPrivate());
		conversation.setQuestion(false);

		return conversationRepository.save(conversation);
	}

	@Transactional
	public void deleteAnswer(Long answerId) {
		Conversation answer = findConversationById(answerId);
		conversationRepository.delete(answer);
	}

	private Conversation findConversationById(Long conversationId) {
		return conversationRepository.findById(conversationId)
									.orElseThrow(() -> new IllegalArgumentException("존재하지않는 대화입니다."));
	}

	private ConversationResponse convertToResponse(Conversation conversation) {
		return new ConversationResponse(
			conversation.getId(),
			conversation.getConversationMST().getOwner().getId(),
			conversation.getConversationMST().getGuest() != null ? conversation.getConversationMST().getGuest().getId() : null,
			conversation.getContent(),
			conversation.isGood(),
			conversation.isPrivate(),
			conversation.isQuestion(),
			conversation.getConversationMST().getId()
		);
	}

}
