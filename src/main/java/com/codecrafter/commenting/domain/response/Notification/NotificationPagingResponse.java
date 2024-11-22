package com.codecrafter.commenting.domain.response.Notification;

import java.util.List;

public record NotificationPagingResponse(
    List<NotificationResponse> notificationResponses,
    boolean lastPage
) {
    public NotificationPagingResponse(List<NotificationResponse> notificationResponses, boolean lastPage) {
        this.notificationResponses = notificationResponses;
        this.lastPage = lastPage;
    }
}
