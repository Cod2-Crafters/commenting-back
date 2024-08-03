package com.codecrafter.commenting.service;

import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.ConversationMST;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.enumeration.NotificationType;
import com.codecrafter.commenting.domain.request.conversation.CreateConversationRequest;
import com.codecrafter.commenting.domain.request.conversation.UpdateConversationRequest;
import com.codecrafter.commenting.domain.response.conversation.ConversationDetailsResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationPageResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationResponse;
import com.codecrafter.commenting.repository.MemberInfoRepository;
import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecrafter.commenting.repository.conversation.ConversationMSTRepository;
import com.codecrafter.commenting.repository.conversation.ConversationRepository;

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
	private final NotificationService notificationService;
	static final int timelinePageSize = 3;

	@Transactional(readOnly = true)
	public ConversationResponse getConversation(Long id) {
		Conversation conversation = findConversationById(id);
		return convertToResponse(conversation);
	}

	@Transactional(readOnly = true)
	public List<ConversationDetailsResponse> getConversationDetails(Long mstId, MemberAuth memberAuth) {
		Long userId = (memberAuth != null) ? memberAuth.getId() : null;
		return conversationRepository.findConversationDetailsByMstId(mstId, userId);
	}

	@Transactional(readOnly = true)
	public List<ConversationDetailsResponse> getConversationByOwnerId(Long ownerId, MemberAuth memberAuth) {
		Long userId = (memberAuth != null) ? memberAuth.getId() : null;
		return conversationRepository.findConversationByOwnerId(ownerId, userId);
	}

	public ConversationPageResponse getConversationPage(Long ownerId, Integer page, MemberAuth memberAuth) {
		Long userId = (memberAuth != null) ? memberAuth.getId() : null;
		int pageNumber = (page != null) ? page - 1 : 0;	// 페이지 인덱스
		int pageSize = timelinePageSize;	// 보여줄 블럭 수
		int offset = pageNumber * pageSize;	// 페이징 시작 위치
		boolean lastPage = false;	// 마지막 페이지 여부
		long totalRecords = conversationMSTRepository.countByOwnerId(ownerId);

		// 더이상 페이지가 없으면 마지막페이지
		if(offset + pageSize > totalRecords) {
			lastPage = true;
		}

		List<ConversationDetailsResponse> result = conversationRepository.findConversationByOwnerIdPaging(ownerId, pageSize, offset, userId);
		return new ConversationPageResponse(result, lastPage);
	}

	@Transactional
	public List<ConversationResponse> createConversation(CreateConversationRequest request) {
		MemberInfo owner = memberInfoRepository.findById(request.ownerId())
												.orElseThrow(() -> new IllegalArgumentException("존재하지않는 프로필입니다."));
		// 익명의 사용자일 경우 널처리
		MemberInfo guest = Optional.ofNullable(request.guestId())
									.flatMap(memberInfoRepository::findById)
									.orElse(null);

		// 변경전 대화 마스터 최대값
		Long maxId = Optional.ofNullable(conversationMSTRepository.findMaxId()).orElse(0L);

		// 대화마스터 저장
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

		// 대화슬레이브 저장
		Long id = conversationRepository.save(conversation).getId();

		// 알림 보내기
		notificationService.saveAndSendNotification(owner, writerInfo, NotificationType.QUESTION, id);

		List<ConversationResponse> conversations = conversationRepository.findByConversationAdd(maxId, id)
																			.stream()
																			.map(this::mapToConversationResponse)
																			.collect(Collectors.toList());
		return conversations;
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
	public Conversation addAnswer(CreateConversationRequest request) {
		ConversationMST conversationMST = conversationMSTRepository.findById(request.mstId())
																	.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대화입니다."));
		MemberInfo writer = memberInfoRepository.findById(request.ownerId())
																	.orElseThrow(() -> new IllegalArgumentException("스페이스 주인을 찾을 수 없습니다."));
		MemberInfo guest = memberInfoRepository.findById(request.guestId())
												.orElseThrow(() -> new IllegalArgumentException("스페이스 주인을 찾을 수 없습니다."));

		Conversation answer = Conversation.builder()
											.content(request.content())
											.isPrivate(request.isPrivate())
											.isQuestion(false) // true = 질문, false = 답변
											.memberInfo(writer)
											.build();

		answer.setConversationMST(conversationMST);
		Conversation conversation = conversationRepository.save(answer);
		notificationService.saveAndSendNotification(guest, writer, NotificationType.COMMENT, conversation.getId());
		return conversation;
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

	private Conversation findConversationById(Long conId) {
		return conversationRepository.findById(conId)
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
	private ConversationResponse mapToConversationResponse(Tuple tuple) {
		return new ConversationResponse(
			tuple.get("conId", Long.class),
			tuple.get("ownerId", Long.class),
			tuple.get("guestId", Long.class),
			tuple.get("content", String.class),
			tuple.get("isGood", Boolean.class),
			tuple.get("isPrivate", Boolean.class),
			tuple.get("isQuestion", Boolean.class),
			tuple.get("mstId", Long.class)
		);
	}


}
