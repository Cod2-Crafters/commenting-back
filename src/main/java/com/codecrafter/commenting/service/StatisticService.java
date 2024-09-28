package com.codecrafter.commenting.service;

import com.codecrafter.commenting.config.SecurityUtil;
import com.codecrafter.commenting.domain.entity.ConversationMST;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.response.QuestionedUser;
import com.codecrafter.commenting.domain.response.QuestionedUserInterface;
import com.codecrafter.commenting.domain.response.StatisticsResponse;
import com.codecrafter.commenting.repository.conversation.ConversationMSTRepository;
import com.codecrafter.commenting.repository.conversation.ConversationRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService {

    private final ConversationMSTRepository conversationMSTRepository;
    private final ConversationRepository conversationRepository;
    //TODO: 내가 쓴 질문 중 좋은 질문 받은 개수        mst 게스트 + recommend에 like
    // 지금까지 받은 질문                           mst 오너
    // 지금 까지 보낸 질문                          mst 게스트
    // 지금 까지 쓴 답변                           mst 오너
    // 아직 답하지 않은 질문                         mst 오너 + mst 1개
    // 나의 답변률
    // 지금 까지 내가 질문한 사람들 list로

    @Transactional(readOnly = true)
    public StatisticsResponse getStatistics() {
        MemberInfo memberInfo = SecurityUtil.getCurrentMember().getMemberInfo();

        List<ConversationMST> guestConversationMST = conversationMSTRepository.findByGuestId(memberInfo.getId()); // 내가 게스트인  방 -> 질문 방 수 내가 한 질문의 수량
        List<ConversationMST> ownerConversationMST = conversationMSTRepository.findByOwnerId(memberInfo.getId()); // 내가 오너인 방 -> 답변 방 수, 받은 질문 의 수랑 같다

        //받은 좋을질문
        long goodQuestionCount = conversationRepository.countGoodQuestionByMemberId(memberInfo.getId());

        //받은 질문 수
        long receivedQuestionCount = ownerConversationMST.size();

        // 보낸 질문 수
        long sentQuestionCount = guestConversationMST.size();

        // 쓴 답변
        long answerCount = conversationRepository.countByMemberInfoIdAndIsQuestionFalse(memberInfo.getId());

        // 답하지 않은 질문
        long unansweredQuestionCount = conversationMSTRepository.countUnansweredQuestionByMemberId(memberInfo.getId());

        // 나의 답변률 = (받은 질문 - 답하지 않은 질문) / 받은 질문
        double answerRate = (receivedQuestionCount - unansweredQuestionCount) / (double)receivedQuestionCount * 100;

        List<QuestionedUserInterface> questionedUsersInterface = conversationMSTRepository.findQuestionedUserByMemberId(memberInfo.getId());
        List<QuestionedUser> questionedUsers = new ArrayList<>();
        questionedUsersInterface.forEach(e -> questionedUsers.add(new QuestionedUser(e)));

        return StatisticsResponse.builder()
                                .goodQuestionCount(goodQuestionCount)
                                .receivedQuestionCount(receivedQuestionCount)
                                .sentQuestionCount(sentQuestionCount)
                                .answerCount(answerCount)
                                .unansweredQuestionCount(unansweredQuestionCount)
                                .answerRate(answerRate)
                                .questionedUsers(questionedUsers)
                                .build();
    }

}
