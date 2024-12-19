package com.codecrafter.commenting.service;

import com.codecrafter.commenting.common.util.SecurityUtil;
import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.entity.Notification;
import com.codecrafter.commenting.domain.enumeration.NotificationType;
import com.codecrafter.commenting.domain.enumeration.Period;
import com.codecrafter.commenting.domain.request.ReadNotificationRequest;
import com.codecrafter.commenting.domain.response.Notification.NotificationPagingResponse;
import com.codecrafter.commenting.domain.response.Notification.NotificationResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationDetailsResponse;
import com.codecrafter.commenting.repository.EmitterRepository;
import com.codecrafter.commenting.repository.NotificationQuerydslRepository;
import com.codecrafter.commenting.repository.NotificationRepository;
import com.codecrafter.commenting.repository.conversation.ConversationRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@EnableAsync
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 10;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final ConversationRepository conversationRepository;
    private final NotificationQuerydslRepository notificationQuerydslRepository;

    public SseEmitter subscribe(String email, String lastEventId) {
        String emitterId = makeTimeIncludeId(email);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitter.complete()); // 필수
        emitter.onError(e -> {
            emitterRepository.deleteById(emitterId); // 필수
        });

        long unreadCount = notificationRepository.countByIsReadFalseAndReceiverInfoEmail(email);

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(email);
        sendToClient(emitter, eventId, emitterId, "EventStream Created. [userEmail=" + email + "]", unreadCount);

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, email, emitterId, emitter);
        }

        return emitter;
    }

    private String makeTimeIncludeId(String email) {
        return email + "_" + System.currentTimeMillis();
    }

    private void sendToClient(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                .id(eventId)
                .name("sse")
                .data(data)
            );
        } catch (IOException | IllegalStateException e) {}
    }

    private void sendToClient(SseEmitter emitter, String eventId, String emitterId, Object data, Long unreadCount) {
        try {
            emitter.send(SseEmitter.event()
                .id(eventId)
                .name("sse")
                .data(data)
                .data(Map.of("unreadCount", unreadCount))
            );
        } catch (IOException | IllegalStateException e) {}
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, String userEmail, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(userEmail));
        eventCaches.entrySet().stream()
            .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
            .forEach(entry -> sendToClient(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    @Async("notificationTaskExecutor")
    public void saveAndSendNotification(MemberInfo receiver, MemberInfo sender, NotificationType type, Conversation conversation) {
        Notification notification = notificationRepository.save(createNotification(receiver, sender, type, conversation.getId()));
        String receiverEmail = receiver.getEmail();
        String eventId = makeTimeIncludeId(receiverEmail);
        long countIsRead = notificationRepository.countByIsReadFalseAndReceiverInfo(receiver);

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverEmail);
        if (emitters.isEmpty()) {
            return;
        }
        emitters.forEach(
            (key, emitter) -> {
                NotificationResponse notificationResponse = toNotificationResponse(notification, conversation);
                emitterRepository.saveEventCache(key, notificationResponse);
                sendToClient(emitter, eventId, key, notificationResponse, countIsRead);
            }
        );
    }

    private NotificationResponse toNotificationResponse(Notification notification, Conversation conversation) {
        MemberInfo sender = conversation.getMemberInfo();
        String senderName = sender == null ? "익명회원" : sender.getNickname();
        String senderAvatarPath = sender == null ? null : sender.getAvatarPath();

        return NotificationResponse.builder()
                                    .id(notification.getId())
                                    .senderNickName(senderName) // 상대 닉네임이
                                    .message(notification.getMessage())
                                    .content(conversation.getContent())  // 질문, 답변 내용
                                    .type(notification.getNotificationType())
                                    .createdAt(notification.getCreatedAt())
                                    .url(notification.getUrl())
                                    .image(senderAvatarPath) // 상대 이미지
                                    .isRead(notification.getIsRead())
                                    .mstId(conversation.getConversationMST().getId())
                                    .build();
    }

    private Notification createNotification(MemberInfo receiver, MemberInfo sender, NotificationType type, Long typeId) {
        String senderName = sender == null ? "익명회원" : sender.getNickname();
        String message = "";
        String url = "";

        switch (type) {
            case LIKES -> {
                message = senderName + "님이 회원님의 글을 좋아합니다.";
                url = "/api/conversations/question/" + typeId;
            }
            case COMMENT -> {
                message = senderName + "님이 회원님의 글에 답변을 남겼습니다.";
                url = "/api/conversations/question/" + typeId;
            }
            case THANKED -> {
                message = senderName + "님이 회원님의 글을 고마워합니다.";
                url = "/api/conversations/question/" + typeId;
            }
            case QUESTION -> {
                message = senderName + "님이 회원님께 질문을 남겼습니다.";
                url = "/api/conversations/question/" + typeId;
            }
        }

        return Notification.builder()
                            .receiverInfo(receiver)
                            .notificationType(type)
                            .message(message)
                            .url(url)
                            .isRead(false)
                            .notificationTypeId(typeId)
                            .build();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications() {
        Long getCurrentMemberId = SecurityUtil.getCurrentMember().getMemberInfo().getId(); // 현재 사용자
        return notificationRepository.findByReceiverId(getCurrentMemberId);
    }

    @Transactional(readOnly = true)
    public NotificationPagingResponse getNotifications(Period period, Long cursor) { // period=1week&periodStart=20241115&periodEnd=20241122  // 1week = 7 1month 30 3month 90  6month  180 1year 360
        Long getCurrentMemberId = SecurityUtil.getCurrentMember().getMemberInfo().getId(); // 현재 사용자
        boolean lastPage = true;

        List<NotificationResponse> notificationResponses = notificationQuerydslRepository.findByReceiverIdAndPeriod(getCurrentMemberId, period, cursor);

        if ((cursor == null && notificationResponses.size() == 16) // 첫 조회 갯수는 16개, 반환은 15개
            || (cursor != null && notificationResponses.size() == 6)) { // 첫 조회 아니면 갯수는 6개, 반환은 5개
            lastPage = false;
            notificationResponses.remove(notificationResponses.size() - 1);
        }

        return new NotificationPagingResponse(notificationResponses, lastPage);
    }

    @Transactional
    public List<NotificationResponse> markAllNotificationsAsRead() {
        Long getCurrentMemberId = SecurityUtil.getCurrentMember().getMemberInfo().getId(); // 현재 사용자
        notificationRepository.markAllNotificationsAsRead(getCurrentMemberId);
        return notificationRepository.findByReceiverId(getCurrentMemberId);
    }

    @Transactional
    public NotificationPagingResponse markAllNotificationsAsRead(Period period) {
        Long getCurrentMemberId = SecurityUtil.getCurrentMember().getMemberInfo().getId(); // 현재 사용자
        notificationRepository.markAllNotificationsAsRead(getCurrentMemberId);
        List<NotificationResponse> notificationResponses =  notificationQuerydslRepository.findByReceiverIdAndPeriod(getCurrentMemberId, period, null); // 읽음처리 후 첫페이지로
        boolean lastPage = true;

        if (notificationResponses.size() == 16)  { // 첫 조회 갯수는 16개, 반환은 15개
            lastPage = false;
            notificationResponses.remove(notificationResponses.size() - 1);
        }
        return new NotificationPagingResponse(notificationResponses, lastPage);
    }

    @Transactional
    public List<ConversationDetailsResponse> getConversationsAndMarkNotificationAsRead(
        ReadNotificationRequest readNotificationRequest,
        Long notificationId
    ) {
        Long getCurrentMemberId = SecurityUtil.getCurrentMember().getMemberInfo().getId();

        if (!readNotificationRequest.isRead()) {
            Notification notification = notificationRepository.findById(notificationId).orElseThrow();
            notification.markAsRead();
        }
        return conversationRepository.findConversationDetailsByMstId(readNotificationRequest.mstId(), getCurrentMemberId);
    }

    @Transactional
    public void deleteNotification(Long id) {
        Long getCurrentMemberId = SecurityUtil.getCurrentMember().getMemberInfo().getId();
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 알림을 찾을 수 없습니다."));
        Long receiverId = notification.getReceiverInfo().getId();

        if (!getCurrentMemberId.equals(receiverId)) {
            throw new AccessDeniedException("알림 삭제 권한이 없습니다.");
        }
        notificationRepository.deleteById(id);
    }

}
