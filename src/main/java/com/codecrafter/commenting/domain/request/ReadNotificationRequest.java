package com.codecrafter.commenting.domain.request;

public record ReadNotificationRequest(
     Long mstId,
     Boolean isRead
) {

}
