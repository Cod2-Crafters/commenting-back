package com.codecrafter.commenting.service;

import com.codecrafter.commenting.config.SecurityUtil;
import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.entity.Notification;
import com.codecrafter.commenting.domain.enumeration.NotificationType;
import com.codecrafter.commenting.domain.response.Notification.NotificationResponse;
import com.codecrafter.commenting.repository.EmitterRepository;
import com.codecrafter.commenting.repository.NotificationRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 10;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    public SseEmitter subscribe(String email, String lastEventId) {
        String emitterId = makeTimeIncludeId(email);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitter.complete()); // 필수
        emitter.onError(e -> {
            emitterRepository.deleteById(emitterId); // 필수
        });


        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(email);
        sendToClient(emitter, eventId, emitterId, "EventStream Created. [userEmail=" + email + "]");

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
        } catch (IOException | IllegalStateException e) {
        }
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

    @Transactional
    public void saveAndSendNotification(MemberInfo receiver, MemberInfo sender, NotificationType type, Conversation conversation) {
        Notification notification = notificationRepository.save(createNotification(receiver, sender, type, conversation.getId()));
        String receiverEmail = receiver.getEmail();
        String eventId = makeTimeIncludeId(receiverEmail);

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverEmail);
        if (emitters.isEmpty()) {
            return;
        }
        emitters.forEach(
            (key, emitter) -> {
                NotificationResponse notificationResponse = toNotificationResponse(notification, conversation);
                emitterRepository.saveEventCache(key, notificationResponse);
                sendToClient(emitter, eventId, key, notificationResponse);
            }
        );
    }

    private NotificationResponse toNotificationResponse(Notification notification, Conversation conversation) {
        return NotificationResponse.builder()
                                    .id(notification.getId())
                                    .senderNickName(conversation.getMemberInfo().getNickname()) // 상대 닉네임이
                                    .message(notification.getMessage())
                                    .content(conversation.getContent())  // 질문, 답변 내용
                                    .type(notification.getNotificationType())
                                    .createdAt(notification.getCreatedAt())
                                    .url(notification.getUrl())
                                    .image(conversation.getMemberInfo().getAvatarPath()) // 상대 이미지
                                    .isRead(notification.getIsRead())
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

    @Transactional
    public List<NotificationResponse> markAllNotificationsAsRead() {
        Long getCurrentMemberId = SecurityUtil.getCurrentMember().getMemberInfo().getId(); // 현재 사용자
        notificationRepository.markAllNotificationsAsRead(getCurrentMemberId);
        return notificationRepository.findByReceiverId(getCurrentMemberId);
    }
}
