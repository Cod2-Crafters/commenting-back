package com.codecrafter.commenting.aop;

import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.enumeration.NotificationType;
import com.codecrafter.commenting.domain.request.RecommendRequest;
import com.codecrafter.commenting.domain.response.RecommendResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationProfileResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationResponse;
import com.codecrafter.commenting.repository.MemberInfoRepository;
import com.codecrafter.commenting.repository.conversation.ConversationRepository;
import com.codecrafter.commenting.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
//@EnableAsync
@RequiredArgsConstructor
public class NotificationAspect {

    private final NotificationService notificationService;
    private final MemberInfoRepository memberInfoRepository;
    private final ConversationRepository conversationRepository;

    @Pointcut("@annotation(com.codecrafter.commenting.annotation.Notification)")
    public void annotationPointcut() {
    }

//    @Async // TODO: 비동기로 수정
    @Transactional
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
                        Conversation conversation = conversationRepository.findById(conId).orElse(null);
                        notificationService.saveAndSendNotification(owner, guest, NotificationType.QUESTION, conversation);
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
                    Conversation conversation = conversationRepository.findById(conId).orElse(null);
                    notificationService.saveAndSendNotification(guest, owner, NotificationType.COMMENT, conversation);
                }
            }
            // 조와요
            case "updateLikes" -> {
                if (result instanceof RecommendResponse recommendResponse) {
                    RecommendRequest request = (RecommendRequest) joinPoint.getArgs()[0];
                    Long conId = request.conId();
                    Long guestId = request.userId();
                    if (recommendResponse.action().equals("insert")) {
                        Conversation conversation = conversationRepository.findById(conId).orElse(null);
                        Long ownerId = conversation.getMemberInfo().getId();
                        MemberInfo guest = memberInfoRepository.findById(guestId).orElse(null);
                        MemberInfo owner = memberInfoRepository.findById(ownerId).orElse(null);
                        notificationService.saveAndSendNotification(owner, guest, NotificationType.LIKES, conversation);
                    }
                }
            }
            // 고마와요
            case "updateThanked" -> {
                if (result instanceof RecommendResponse recommendResponse) {
                    RecommendRequest request = (RecommendRequest) joinPoint.getArgs()[0];
                    Long conId = request.conId();
                    Long guestId = request.userId();
                    if (recommendResponse.action().equals("insert")) {
                        Conversation conversation = conversationRepository.findById(conId).orElse(null);
                        Long ownerId = conversation.getMemberInfo().getId();
                        MemberInfo guest = memberInfoRepository.findById(guestId).orElse(null);
                        MemberInfo owner = memberInfoRepository.findById(ownerId).orElse(null);
                        notificationService.saveAndSendNotification(owner, guest, NotificationType.THANKED, conversation);
                    }
                }
            }
        }
    }
}
