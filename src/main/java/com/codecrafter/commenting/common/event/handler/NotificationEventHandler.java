package com.codecrafter.commenting.common.event.handler;

import com.codecrafter.commenting.common.event.dto.NotificationEvent;
import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.enumeration.NotificationType;
import com.codecrafter.commenting.service.MailSendService;
import com.codecrafter.commenting.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventHandler {

    private final NotificationService notificationService;
    private final MailSendService mailSendService;

    @TransactionalEventListener
    public void notification(NotificationEvent notificationEvent) {
        Conversation conversation = notificationEvent.getConversation();
        MemberInfo sender = notificationEvent.getSender();
        MemberInfo receiver = notificationEvent.getReceiver();
        NotificationType notificationType = notificationEvent.getNotificationType();

        // TODO: 해결 방법 찾기
        // could not initialize proxy - no Session 에러 임시처리 (강제 초기화 방법)
        // 1. Hibernate.initialize(receiver);
        // 2. ↓
        receiver.getEmail(); // 강제 초기화

        notificationService.saveAndSendNotification(receiver, sender, notificationType, conversation);

        if (notificationType == NotificationType.QUESTION || notificationType == NotificationType.COMMENT) {
            if (receiver != null && receiver.getMemberSetting().getEmailNotice()) {
                HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String domainName = mailSendService.getDomainName(httpServletRequest);
                mailSendService.sendEmailNotice(
                    receiver.getEmail(),
                    domainName,
                    notificationType.label(),
                    "/api/conversations/details/" + conversation.getConversationMST().getId(),
                    conversation.getContent(),
                    receiver.getNickname()
                );
            }
        }
    }
}
