package com.codecrafter.commenting.domain.response.conversation;

public record ConversationResponse(
    Long conId,
    Long ownerId,
    Long guestId,
    String content,
    Boolean isGood,
    boolean isPrivate,
    boolean isQuestion,
    Long mstId
) {

}
