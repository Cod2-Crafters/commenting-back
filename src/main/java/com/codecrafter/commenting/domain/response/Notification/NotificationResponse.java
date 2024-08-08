package com.codecrafter.commenting.domain.response.Notification;

import com.codecrafter.commenting.domain.enumeration.NotificationType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationResponse(
    Long id,
    String senderNickName,
    String message,
    String content,
    NotificationType type,
    LocalDateTime createdAt,
    String url,
    String image,
    Boolean isRead
) {

}
