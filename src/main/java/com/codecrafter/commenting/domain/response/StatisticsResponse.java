package com.codecrafter.commenting.domain.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatisticsResponse {

    private Long goodQuestionCount;
    private Long receivedQuestionCount;
    private Long sentQuestionCount;
    private Long answerCount;
    private Long unansweredQuestionCount;
    private Double answerRate;
    private List<QuestionedUser> questionedUsers;
}
