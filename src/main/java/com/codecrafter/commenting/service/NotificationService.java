package com.codecrafter.commenting.service;

import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.entity.Notification;
import com.codecrafter.commenting.domain.enumeration.NotificationType;
import com.codecrafter.commenting.domain.response.Notification.NotificationResponse;
import com.codecrafter.commenting.repository.EmitterRepository;
import com.codecrafter.commenting.repository.NotificationRepository;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    public SseEmitter subscribe(String email, String lastEventId) {
        String emitterId = makeTimeIncludeId(email);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

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
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            throw new RuntimeException("Connection Failed.");
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

    public void saveAndSendNotification(MemberInfo receiver, MemberInfo sender, NotificationType type, Long typeId) {
        Notification notification = notificationRepository.save(createNotification(receiver, sender, type, typeId));
        String receiverEmail = receiver.getEmail();
        String eventId = makeTimeIncludeId(receiverEmail);

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverEmail);
        emitters.forEach(
            (key, emitter) -> {
                emitterRepository.saveEventCache(key, notification);
                sendToClient(emitter, eventId, key, toNotificationResponse(notification));
            }
        );
    }

    private NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .name(notification.getReceiverInfo().getNickname())
            .message(notification.getMessage())
            .type(notification.getNotificationType())
            .createdAt(notification.getCreatedAt())
            .url(notification.getUrl())
            .build();
    }

        private Notification createNotification(MemberInfo receiver, MemberInfo sender, NotificationType type, Long typeId) {
            String message = "";
            String url = "";

            Notification.NotificationBuilder builder = Notification.builder();
            builder
                .receiverInfo(receiver)
                .isRead(false)
                .notificationType(type);

            if (NotificationType.LIKES == type) {
                message = sender.getNickname() + "님이 회원님의 글을 좋아합니다.";
                url = "/api/conversations/question/" + typeId;
                builder
                    .message(message)
                    .url(url);

            } else if (NotificationType.COMMENT == type) {
                message = sender.getNickname() + "님이 회원님의 글에 답변을 남겼습니다.";
                url = "/api/conversations/question/" + typeId;
                builder
                    .message(message)
                    .url(url);
            } else if (NotificationType.THANKED == type) {
                message = sender.getNickname() + "님이 회원님의 글을 고마워합니다.";
                url = "/api/conversations/question/" + typeId;
                builder
                    .message(message)
                    .url(url);
            } else if(NotificationType.QUESTION == type){
                message = sender.getNickname() + "님이 회원님께 질문을 남겼습니다.";
                url = "/api/conversations/question/" + typeId;
                builder
                    .message(message)
                    .url(url);
            }

        return builder.build();
    }

}
