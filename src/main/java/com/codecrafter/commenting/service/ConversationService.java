package com.codecrafter.commenting.service;

import com.codecrafter.commenting.annotation.Notification;
import com.codecrafter.commenting.config.SecurityUtil;
import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.ConversationMST;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.request.conversation.CreateConversationRequest;
import com.codecrafter.commenting.domain.request.conversation.UpdateConversationRequest;
import com.codecrafter.commenting.domain.response.conversation.ConversationDetailsResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationPageResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationProfileResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationResponse;
import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Transactional
public class ConversationService {

	private final ConversationMSTRepository conversationMSTRepository;
	private final ConversationRepository conversationRepository;
	private final NotificationService notificationService;
	static final int timelinePageSize = 3;

	/**
	 * conId로 질문 혹은 답변 1건을 조회합니다.
	 *
	 * @param conId 대화 슬레이브 ID
	 * @return 대화 응답 객체
	 */
	@Transactional(readOnly = true)
	public ConversationProfileResponse getConversation(Long conId) {
		Long userId = getCurrentUserId();
		Tuple tuple = findConversationByIdWithWriter(conId, userId);
		if (tuple == null) {
			throw new IllegalArgumentException("존재하지 않는 대화입니다.");
		}
		return mapToConversationResponse(tuple);
	}

	/**
	 * 대화 블럭 1건을 조회합니다.
	 *
	 * @param mstId 대화 마스터 ID
	 * @return 대화 상세 응답 객체 목록
	 */
	@Transactional(readOnly = true)
	public List<ConversationDetailsResponse> getConversationDetails(Long mstId) {
		Long userId = getCurrentUserId();
		return conversationRepository.findConversationDetailsByMstId(mstId, userId);
	}

	/**
	 * 답변자 ID로 대화를 조회합니다.
	 *
	 * @param ownerId 답변자 ID
	 * @return 대화 상세 응답 객체 목록
	 */
	@Transactional(readOnly = true)
	public List<ConversationDetailsResponse> getConversationByOwnerId(Long ownerId) {
		Long userId = getCurrentUserId();
		return conversationRepository.findConversationByOwnerId(ownerId, userId);
	}

	/**
	 * 대화 페이지를 조회합니다.
	 *
	 * @param ownerId 답변자 ID
	 * @param page 페이지 번호
	 * @return 대화 페이지 응답 객체
	 */
	public ConversationPageResponse getConversationPage(Long ownerId, Integer page) {
		Long userId = getCurrentUserId();
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

	/**
	 * 질문을 작성합니다.
	 *
	 * @param request 대화 생성 요청 객체
	 * @return 대화/프로필 응답 객체 목록
	 */
	@Transactional
	@Notification
	public List<ConversationProfileResponse> createConversation(CreateConversationRequest request) {
		MemberInfo owner = MemberInfo.builder()
										.id(request.ownerId())
//										.nickname("")
//										.email("")
										.build();
		MemberInfo guest = SecurityUtil.getCurrentMember().getMemberInfo();

		// 변경전 대화 마스터 최대값
		Long maxId = Optional.ofNullable(request.maxMstId()).orElse(0L);

		// 대화마스터 저장
		ConversationMST conversationMST = ConversationMST.create(owner, guest);
		conversationMST = conversationMSTRepository.save(conversationMST);

		Conversation conversation = Conversation.builder()
												.content(request.content())
												.isPrivate(request.isPrivate())
												.isQuestion(true) // true = 질문, false = 답변
												.memberInfo(guest)
												.build();
		conversation.setConversationMST(conversationMST);

		// 대화슬레이브 저장
		Long id = conversationRepository.save(conversation).getId();

		return conversationRepository.findByConversationAdd(maxId, id)
										.stream()
										.map(this::mapToConversationResponse)
										.collect(Collectors.toList());
	}

	/**
	 * 질문 혹은 답변 1건을 수정합니다.
	 *
	 * @param request 대화 업데이트 요청 객체
	 * @return 업데이트된 대화 객체
	 */
	@Transactional
	public ConversationResponse updateConversation(UpdateConversationRequest request) {
		Conversation conversation = findConversationById(request.conId());

		conversation.setContent(request.content());
		conversation.setPrivate(request.isPrivate());
		conversation.setQuestion(true);

		conversationRepository.save(conversation);

		return convertToResponse(conversation);
	}

	/**
	 * 대화블럭 1건을 삭제합니다
	 *
	 * @param mstId 대화 마스터 ID
	 */
	@Transactional
	public void deleteConversationAndDetails(Long mstId) {
		conversationRepository.deleteByConversationMSTId(mstId);
		conversationMSTRepository.deleteById(mstId);
	}

	/**
	 * 답변을 작성합니다.
	 *
	 * @param request 대화 생성 요청 객체
	 * @return 추가된 답변 객체
	 */
	@Transactional
	@Notification
	public ConversationResponse addAnswer(CreateConversationRequest request) {
		ConversationMST conversationMST = conversationMSTRepository.findById(request.mstId())
																	.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대화입니다."));

		MemberInfo writer = SecurityUtil.getCurrentMember().getMemberInfo();
		Conversation answer = Conversation.builder()
											.content(request.content())
											.isPrivate(request.isPrivate())
											.isQuestion(false) // true = 질문, false = 답변
											.memberInfo(writer)
											.build();

		answer.setConversationMST(conversationMST);
		Conversation conversation = conversationRepository.save(answer);
		return convertToResponse(conversation);
	}


	/**
	 * 답변을 수정합니다.
	 *
	 * @param request 대화 수정 요청 객체
	 */
	public ConversationResponse updateAddAnswer(UpdateConversationRequest request) {
		Conversation conversation = findConversationById(request.conId());

		conversation.setContent(request.content());
		conversation.setPrivate(request.isPrivate());
		conversation.setQuestion(false);

		conversationRepository.save(conversation);

		return convertToResponse(conversation);
	}

	/**
	 * 답변을 삭제합니다.
	 *
	 * @param answerId 답변 ID
	 */
	@Transactional
	public void deleteAnswer(Long answerId) {
		Conversation answer = findConversationById(answerId);
		if(answer.isQuestion()) {
			throw new IllegalArgumentException("질문은 삭제할 수 없습니다.");
		}
		conversationRepository.delete(answer);
	}

	/**
	 * 보낸 질문을 조회합니다.
	 *
	 * @param guestId 주인 ID
	 */
	@Transactional(readOnly = true)
	public List<ConversationProfileResponse> getQuestionsByGuestId(Long guestId) {
		return conversationRepository.findByGuestId(guestId)
										.stream()
										.map(this::mapToConversationResponse)
										.collect(Collectors.toList());
	}

	private Conversation findConversationById(Long conId) {
		return conversationRepository.findById(conId)
									.orElseThrow(() -> new IllegalArgumentException("존재하지않는 대화입니다."));
	}

	public Tuple findConversationByIdWithWriter(Long conId, Long userId) {
		return conversationRepository.findConversationResponseById(conId, userId);
	}

	private ConversationResponse convertToResponse(Conversation conversation) {
		return new ConversationResponse(conversation.getId(),
										conversation.getConversationMST().getOwner().getId(),
										conversation.getConversationMST().getGuest() != null ? conversation.getConversationMST().getGuest().getId() : 0,
										conversation.getContent(),
										conversation.isGood(),
										conversation.isThanked(),
										conversation.isPrivate(),
										conversation.isQuestion(),
										conversation.getConversationMST().getId()
		);
	}

	private ConversationProfileResponse mapToConversationResponse(Tuple tuple) {
		return new ConversationProfileResponse(	tuple.get("conId", Long.class),
												tuple.get("ownerId", Long.class),
												tuple.get("guestId", Long.class),
												tuple.get("content", String.class),
												tuple.get("isGood", Boolean.class),
												tuple.get("isThanked", Boolean.class),
												tuple.get("isPrivate", Boolean.class),
												tuple.get("isQuestion", Boolean.class),
												tuple.get("mstId", Long.class),
												tuple.get("avatarPath", String.class)
		);
	}

	private Long getCurrentUserId() {
		return SecurityUtil.getCurrentMember().getId();
	}
}
