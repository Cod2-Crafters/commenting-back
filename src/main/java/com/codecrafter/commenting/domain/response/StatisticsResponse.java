package com.codecrafter.commenting.domain.response;

import java.util.List;
import lombok.Getter;

@Getter
public class StatisticsResponse {

    private Long goodQuestionCount;
    private Long receivedQuestionCount;
    private Long sentQuestionCount;
    private Long answerCount;
    private Long unansweredQuestionCount;
    private Double answerRate;
    private List<QuestionedUser> questionedUsers;

    public StatisticsResponse(Long goodQuestionCount, Long receivedQuestionCount, Long sentQuestionCount,
        Long answerCount,
        Long unansweredQuestionCount) {
        this.goodQuestionCount = goodQuestionCount;
        this.receivedQuestionCount = receivedQuestionCount;
        this.sentQuestionCount = sentQuestionCount;
        this.answerCount = answerCount;
        this.unansweredQuestionCount = unansweredQuestionCount;
    }

    public void setAnswerRateAndQuestionedUsers(List<QuestionedUser> questionedUsers) {
        this.answerRate = (receivedQuestionCount - unansweredQuestionCount) / (double)receivedQuestionCount * 100;
        this.questionedUsers = questionedUsers;
    }
}
