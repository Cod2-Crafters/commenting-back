package com.codecrafter.commenting.common.event.dto;

import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.enumeration.NotificationType;
import lombok.Getter;

@Getter
public class NotificationEvent {

    private MemberInfo sender;
    private MemberInfo receiver;
    private Conversation conversation;
    private NotificationType notificationType;

    public NotificationEvent(MemberInfo sender, MemberInfo receiver, Conversation conversation, NotificationType notificationType) {
        this.sender = sender;
        this.receiver = receiver;
        this.conversation = conversation;
        this.notificationType = notificationType;
    }
}
