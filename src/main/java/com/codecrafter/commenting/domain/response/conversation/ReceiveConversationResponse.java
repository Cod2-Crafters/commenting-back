package com.codecrafter.commenting.domain.response.conversation;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReceiveConversationResponse {

    private Long mstId;
    private Long guestId;
    private Long ownerId;
    private Long conId;
    private String content;
    private Boolean isGood;
    private Boolean isThanked;
    private Boolean isPrivate;
    private Boolean isQuestion;
    private LocalDateTime modifiedAt;
    private String avatarPath;
    private String nickname;
    private Long writerId;

    public ReceiveConversationResponse(Long mstId, Long guestId, Long ownerId, Long conId, String content, Boolean isGood,
        Boolean isThanked, Boolean isPrivate, Boolean isQuestion, LocalDateTime modifiedAt, String avatarPath,
        String nickname, Long writerId) {
        this.mstId = mstId;
        this.guestId = guestId;
        this.ownerId = ownerId;
        this.conId = conId;
        this.content = content;
        this.isGood = isGood;
        this.isThanked = isThanked;
        this.isPrivate = isPrivate;
        this.isQuestion = isQuestion;
        this.modifiedAt = modifiedAt;
        this.avatarPath = avatarPath;
        this.nickname = nickname;
        this.writerId = writerId;
    }
}
