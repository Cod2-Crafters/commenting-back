package com.codecrafter.commenting.domain.response.Notification;

import com.codecrafter.commenting.domain.enumeration.NotificationType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationResponse(
    Long id,
    String name,
    String message,
    NotificationType type,
    LocalDateTime createdAt,
    String url
) {

}
