package com.codecrafter.commenting.domain.response.conversation;

public record ConversationProfileResponse(
    Long conId,
    Long ownerId,
    Long guestId,
    String content,
    Boolean isGood,
    Boolean isThanked,
    boolean isPrivate,
    boolean isQuestion,
    Long mstId,
    String avatarPath,
    String nickname
) {

}

