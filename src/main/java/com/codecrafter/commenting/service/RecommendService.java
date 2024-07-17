package com.codecrafter.commenting.service;

import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.entity.Recommend;
import com.codecrafter.commenting.domain.enumeration.RecommendStatus;
import com.codecrafter.commenting.domain.request.RecommendRequest;
import com.codecrafter.commenting.domain.response.RecommendResponse;
import com.codecrafter.commenting.repository.ConversationRepository;
import com.codecrafter.commenting.repository.MemberInfoRepository;
import com.codecrafter.commenting.repository.RecommendRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RecommendService {
    private final RecommendRepository recommendRepository;
    private final ConversationRepository conversationRepository;
    private final MemberInfoRepository memberInfoRepository;
    @Transactional
    public RecommendResponse incrementLikes(RecommendRequest request) {
        Conversation conversation = conversationRepository.findById(request.conversationId())
                                                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대화입니다."));
        MemberInfo memberInfo = memberInfoRepository.findById(request.userId())
                                                    .orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        Optional<Recommend> existingRecommend = recommendRepository.findByConversationAndMemberInfo(conversation, memberInfo);

        // 좋아요 추가, 좋아요가 이미 있을 경우 제거
        if (existingRecommend.isPresent()) {
            recommendRepository.delete(existingRecommend.get());
            return new RecommendResponse(true, "delete");
        } else {
            Recommend recommend = Recommend.builder()
                                            .conversation(conversation)
                                            .memberInfo(memberInfo)
                                            .recommendStatus(RecommendStatus.LIKES)
                                            .build();
            recommendRepository.save(recommend);
            return new RecommendResponse(true, "insert");
        }
    }
}
