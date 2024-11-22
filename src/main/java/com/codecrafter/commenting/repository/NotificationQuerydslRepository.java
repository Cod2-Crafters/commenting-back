package com.codecrafter.commenting.repository;

import com.codecrafter.commenting.domain.entity.QConversation;
import com.codecrafter.commenting.domain.entity.QMemberInfo;
import com.codecrafter.commenting.domain.entity.QNotification;
import com.codecrafter.commenting.domain.enumeration.Period;
import com.codecrafter.commenting.domain.response.Notification.NotificationResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationQuerydslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    QNotification notification = QNotification.notification;
    QConversation conversation = QConversation.conversation;
    QMemberInfo memberInfo = QMemberInfo.memberInfo;


    public List<NotificationResponse> findByReceiverIdAndPeriod(Long id, Period period, Long cursor) {
        long limit = (cursor == null) ? 16 : 6; // 첫 조회시 데이터 수 15,  이후 5개
        return jpaQueryFactory.select(
                Projections.constructor(NotificationResponse.class,
                    notification.id,
                    memberInfo.nickname,
                    notification.message,
                    conversation.content,
                    notification.notificationType,
                    notification.createdAt,
                    notification.url,
                    memberInfo.avatarPath,
                    notification.isRead,
                    conversation.conversationMST.id
                ))
            .from(notification)
            .join(conversation).on(
                notification.notificationTypeId.eq(conversation.id),
                conversation.isDeleted.eq(false) // 대화가 제거되면 알림도 없어지는게 맞음 근데 이거는 대화 지워지면 알림을 삭제 추가해야함
            )
            .leftJoin(memberInfo).on(conversation.memberInfo.id.eq(memberInfo.id))
            .where(
                notification.receiverInfo.id.eq(id),
                time(period),
                ltCursor(cursor)
            )
            .limit(limit)
            .orderBy(notification.id.desc())
            .fetch();
    }

    private BooleanExpression ltCursor(Long cursor) {
        if (cursor == null) {
            return null;
        }

        return notification.id.lt(cursor);
    }

    private BooleanExpression time(Period period) {
        if (period == null) { // 기본값 1주일
            return notification.createdAt.goe(LocalDateTime.now().minusWeeks(1));
        }

        LocalDateTime dateTime = switch (period) {
            case WEEK -> LocalDateTime.now().minusWeeks(1);
            case MONTH -> LocalDateTime.now().minusMonths(1);
            case THREE_MONTHS -> LocalDateTime.now().minusMonths(3);
            case SIX_MONTHS -> LocalDateTime.now().minusMonths(6);
            case YEAR -> LocalDateTime.now().minusYears(1);
            case ALL -> null;
        };

        if (dateTime == null) {
            return null;
        }

        return notification.createdAt.goe(dateTime);
    }

}
