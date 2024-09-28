package com.codecrafter.commenting.controller;

import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.request.ReadNotificationRequest;
import com.codecrafter.commenting.domain.response.Notification.NotificationResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationDetailsResponse;
import com.codecrafter.commenting.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 구독 ★",
        description = """
            ★현재 사용자가 알림을 실시간으로 스트리밍하기 위한 구독 설정</br>
            {host}/api/subscribe</br>
            입력값 ==================================</br>
            "Last-Event-ID": 마지막으로 처리된 이벤트의 ID </br>
            =======================================
            """)
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(
        @AuthenticationPrincipal MemberAuth memberAuth,
        @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
        HttpServletResponse response
    ) {
        response.setHeader("X-Accel-Buffering", "no");
        SseEmitter sseEmitter = notificationService.subscribe(memberAuth.getEmail(), lastEventId);
        return sseEmitter;
    }

    @Operation(summary = "알림 목록 조회 ★",
        description = """
            ★로그인한 사용자에게 온 알림 목록 조회</br>
            {host}/api/notifications</br>
            """)
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse> getNotifications() {
        List<NotificationResponse> notificationResponses = notificationService.getNotifications();
        return new ResponseEntity<>(ApiResponse.success(notificationResponses), HttpStatus.OK);
    }

    @Operation(summary = "알림 목록 일괄 읽음 처리 ★",
        description = """
            ★로그인한 사용자가 버튼을 눌러 알림 목록 일괄 읽음 처리</br>
            {host}/api/notifications/mark-read</br>
            """)
    @PutMapping("/notifications/mark-read")
    public ResponseEntity<ApiResponse> markAllNotificationsAsRead() {
        List<NotificationResponse> notificationResponses = notificationService.markAllNotificationsAsRead();
        return new ResponseEntity<>(ApiResponse.success(notificationResponses), HttpStatus.OK);
    }

    @Operation(summary = "알림 읽음 처리 및 조회 ★",
        description = """
            ★로그인한 사용자가 알림을 눌러 읽음 처리를 하고 해당 내용을 반환합니다.</br>
            {host}/api/notifications/{notificationId}/mark-read</br>
            """)
    @PostMapping("/notifications/{notificationId}/mark-read")
    public ResponseEntity<ApiResponse> getConversationsAndMarkNotificationAsRead(
        @RequestBody ReadNotificationRequest readNotificationRequest,
        @PathVariable Long notificationId
    ) {
        List<ConversationDetailsResponse> conversations =
                notificationService.getConversationsAndMarkNotificationAsRead(readNotificationRequest, notificationId);
        return ResponseEntity.ok(ApiResponse.success(conversations));
    }
}
