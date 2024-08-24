package com.codecrafter.commenting.domain.response.conversation;

import java.time.LocalDateTime;

public interface ConversationDetailsResponse {
    Long getMstId();
    Long getGuestId();
    Long getOwnerId();
    Long getConId();
    String getContent();
    Boolean getIsGood();
    Boolean getIsThanked();
    Boolean getIsPrivate();
    Boolean getIsQuestion();
    LocalDateTime getModifiedAt();
    String getAvatarPath();
}