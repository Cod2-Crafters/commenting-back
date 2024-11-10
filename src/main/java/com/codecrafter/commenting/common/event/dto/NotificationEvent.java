package com.codecrafter.commenting.common.event.dto;

import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import lombok.Getter;

@Getter
public class NotificationEvent {

    private MemberInfo guest;
    private MemberInfo owner;
    private Conversation conversation;

    public NotificationEvent(MemberInfo guest, MemberInfo owner, Conversation conversation) {
        this.guest = guest;
        this.owner = owner;
        this.conversation = conversation;
    }
}
