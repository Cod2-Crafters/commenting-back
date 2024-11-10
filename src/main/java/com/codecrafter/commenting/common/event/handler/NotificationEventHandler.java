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
    public void question(NotificationEvent notificationEvent) {
        Conversation conversation = notificationEvent.getConversation();
        MemberInfo guest = notificationEvent.getGuest();
        MemberInfo owner = notificationEvent.getOwner();

        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        notificationService.saveAndSendNotification(owner, guest, NotificationType.QUESTION, conversation);
        if (owner.getMemberSetting().getEmailNotice()) {
            String domainName = mailSendService.getDomainName(httpServletRequest);
            mailSendService.sendEmailNotice(owner.getEmail(), domainName, "질문", "/api/conversations/details/" + conversation.getConversationMST().getId(), conversation.getContent(), owner.getNickname());
        }
    }

}
