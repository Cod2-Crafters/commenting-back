package com.codecrafter.commenting.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.ConversationMST;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.request.ConversationRequest;
import com.codecrafter.commenting.domain.response.ConversationMSTResponse;
import com.codecrafter.commenting.repository.ConversationMSTRepository;
import com.codecrafter.commenting.repository.ConversationRepository;
import com.codecrafter.commenting.repository.MemberAuthRepository;
import com.codecrafter.commenting.repository.MemberInfoRepository;

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

	private final MemberAuthRepository memberAuthRepository;
	private final MemberInfoRepository memberInfoRepository;
	private final ConversationMSTRepository conversationMSTRepository;

	public List<ConversationMSTResponse> getAllConversationMST(Long ownerId) {
		List<ConversationMST> conversations = conversationMSTRepository.findByOwnerIdOrderByIdDesc(ownerId);
		List<ConversationMSTResponse> resList = conversations.stream()
			.map(ConversationMSTResponse::fromEntity)
			.toList();

		return resList;
	}

	@Transactional(readOnly = false)
	public void createConversation(ConversationRequest conversationRequest, Long memberId, Long ownerId) {
		MemberInfo ownerInfo = memberInfoRepository.findById(ownerId)
			.orElseThrow(() -> new RuntimeException("없는 사용자에게 요청을 보냈음"));

		MemberInfo memberInfo = memberInfoRepository.findById(memberId).orElse(null);
		if (ownerInfo.getAllowAnonymous() && memberInfo == null) {
			throw new RuntimeException("해당 답변자에게 익명 유저는 글을 쓸 수 없습니다");
		}
		ConversationMST conversationMST;
		Long conversationMSTID = conversationRequest.conversationMSTID();

		if (conversationMSTID == null) { // 새로 만들어야 되는 경우
			conversationMST = conversationMSTRepository.save(ConversationMST.create(ownerInfo, memberInfo));
		} else { //원래 있는 경우
			conversationMST = conversationMSTRepository.findById(conversationMSTID)
				.orElseThrow(() -> new RuntimeException("없는 대화목록에 대화를 추가하려 함"));
		}

		Conversation conversation = Conversation.create(
			conversationRequest.content(),
			conversationRequest.isPrivate(),
			ownerInfo == memberInfo,
			memberInfo
		);
		conversationMST.addConversation(conversation);
	}

}
