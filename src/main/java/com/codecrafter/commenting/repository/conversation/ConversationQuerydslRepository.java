package com.codecrafter.commenting.repository.conversation;

import com.codecrafter.commenting.domain.entity.QConversation;
import com.codecrafter.commenting.domain.entity.QConversationMST;
import com.codecrafter.commenting.domain.entity.QMemberInfo;
import com.codecrafter.commenting.domain.entity.QRecommend;
import com.codecrafter.commenting.domain.enumeration.RecommendStatus;
import com.codecrafter.commenting.domain.response.conversation.ReceiveConversationResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ConversationQuerydslRepository {

    private static final int PAGE_SIZE = 3;

    private final JPAQueryFactory jpaQueryFactory;

    private QConversation conversation = QConversation.conversation;
    private QMemberInfo memberInfo = QMemberInfo.memberInfo;
    private QConversationMST conversationMst = QConversationMST.conversationMST;
    private QRecommend recommend = QRecommend.recommend;

    public List<ReceiveConversationResponse> findReceiveConversations(Long ownerId, Long cursor, Long currentMemberId) {
        List<Long> temp = jpaQueryFactory
            .select(conversationMst.id)
            .from(conversationMst)
            .where(conversationMst.owner.id.eq(ownerId)
                .and(conversationMst.isDeleted.isFalse())
                .and(conversationMst.id.lt(cursor))
            )
            .orderBy(conversationMst.id.desc())
            .limit(PAGE_SIZE)
            .fetch();

        return jpaQueryFactory
            .select(Projections.constructor(
                ReceiveConversationResponse.class,
                conversationMst.id,
                conversationMst.guest.id,
                conversationMst.owner.id,
                conversation.id,
                conversation.content,
                JPAExpressions.select(
                        new CaseBuilder()
                            .when(recommend.count().gt(0))
                            .then(true)  // 고마워요 상태에 대한 조건
                            .otherwise(false)
                    ).from(recommend)
                    .where(recommend.recommendStatus.eq(RecommendStatus.LIKES)
                        .and(recommend.memberInfo.id.eq(currentMemberId))
                        .and(recommend.conversation.id.eq(conversation.id))),
                JPAExpressions.select(
                        new CaseBuilder()
                            .when(recommend.count().gt(0))
                            .then(true)  // 고마워요 상태에 대한 조건
                            .otherwise(false)
                    ).from(recommend)
                    .where(recommend.recommendStatus.eq(RecommendStatus.THANKED)
                        .and(recommend.memberInfo.id.eq(currentMemberId))
                        .and(recommend.conversation.id.eq(conversation.id))),

                conversation.isPrivate,
                conversation.isQuestion,
                conversation.modifiedAt,

                memberInfo.avatarPath,
                memberInfo.nickname,
                memberInfo.id
            ))
            .from(conversationMst)
            .join(conversationMst.conversations, conversation)
            .leftJoin(memberInfo).on(
                (conversation.isQuestion.isFalse().and(conversationMst.owner.id.eq(memberInfo.id)))
                    .or(conversation.isQuestion.isTrue().and(conversationMst.guest.id.eq(memberInfo.id)))
            )
            .where(conversationMst.owner.id.eq(ownerId)
                .and(conversationMst.isDeleted.isFalse())
                .and(conversation.isDeleted.isFalse())
                .and(conversationMst.id.in(temp))
            )
            .orderBy(conversationMst.id.desc(), conversation.id.asc())
            .fetch();
    }

}
