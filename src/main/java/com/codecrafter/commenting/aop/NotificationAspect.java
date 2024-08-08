package com.codecrafter.commenting.aop;

import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.enumeration.NotificationType;
import com.codecrafter.commenting.domain.response.conversation.ConversationProfileResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationResponse;
import com.codecrafter.commenting.repository.MemberInfoRepository;
import com.codecrafter.commenting.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Aspect
@Component
@EnableAsync
@RequiredArgsConstructor
public class NotificationAspect {

    private final NotificationService notificationService;
    private final MemberInfoRepository memberInfoRepository;

    @Pointcut("@annotation(com.codecrafter.commenting.annotation.Notification)")
    public void annotationPointcut() {
    }

    @Async
    @AfterReturning(pointcut = "annotationPointcut()", returning = "result")
    public void checkNotification(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        switch (methodName) {
            // 질문작성
            case "createConversation" -> {
                if (result instanceof List<?> responses) {
                    if (!responses.isEmpty() && responses.get(0) instanceof ConversationProfileResponse) {
                        List<ConversationProfileResponse> list = (List<ConversationProfileResponse>) responses;
                        ConversationProfileResponse conversationProfileResponse = list.get(0);
                        Long guestId = conversationProfileResponse.guestId();
                        Long ownerId = conversationProfileResponse.ownerId();
                        MemberInfo guest = memberInfoRepository.findById(guestId).orElse(null);
                        MemberInfo owner = memberInfoRepository.findById(ownerId).orElse(null);
                        Long conId = conversationProfileResponse.conId();
                        notificationService.saveAndSendNotification(owner, guest, NotificationType.QUESTION, conId);
                    }
                }
            }
            // 답변작성
            case "addAnswer" -> {
                if (result instanceof ConversationResponse conversationResponse) {
                    Long guestId = conversationResponse.guestId();
                    Long ownerId = conversationResponse.ownerId();
                    MemberInfo guest = memberInfoRepository.findById(guestId).orElse(null);
                    MemberInfo owner = memberInfoRepository.findById(ownerId).orElse(null);
                    Long conId = conversationResponse.conId();
                    notificationService.saveAndSendNotification(guest, owner, NotificationType.COMMENT, conId);
                }
            }
        }
    }
}
