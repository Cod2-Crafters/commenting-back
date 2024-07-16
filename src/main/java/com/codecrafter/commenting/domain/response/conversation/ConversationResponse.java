package com.codecrafter.commenting.domain.response.conversation;

public record ConversationResponse(
    Long conversationId,
    Long ownerId,
    Long guestId,
    String content,
    Boolean isGood,
    boolean isPrivate,
    boolean isQuestion,
    Long conversationMstId
) {

}
