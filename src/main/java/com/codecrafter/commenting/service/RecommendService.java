package com.codecrafter.commenting.service;

import com.codecrafter.commenting.annotation.Notification;
import com.codecrafter.commenting.config.jwt.TokenProvider;
import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.entity.Recommend;
import com.codecrafter.commenting.domain.enumeration.RecommendStatus;
import com.codecrafter.commenting.domain.request.RecommendRequest;
import com.codecrafter.commenting.domain.response.RecommendResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationResponse;
import com.codecrafter.commenting.repository.conversation.ConversationRepository;
import com.codecrafter.commenting.repository.MemberInfoRepository;
import com.codecrafter.commenting.repository.RecommendRepository;
import io.jsonwebtoken.Claims;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final TokenProvider tokenProvider;
    @Transactional
    @Notification
    public RecommendResponse updateLikes(RecommendRequest request) {
        Conversation conversation = conversationRepository.findById(request.conId())
                                                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대화입니다."));
        MemberInfo memberInfo = memberInfoRepository.findById(request.userId())
                                                            .orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        Optional<Recommend> existingRecommend = recommendRepository.findRecommend(conversation, memberInfo, RecommendStatus.LIKES);

        // 선택한 대화에 좋아요가 이미 있을 경우 제거
        if (existingRecommend.isPresent()) {
            recommendRepository.delete(existingRecommend.get());
            return new RecommendResponse(true, "delete");
        }
        // 선택한 대화에 좋아요
        else {
            Recommend recommend = Recommend.builder()
                                            .conversation(conversation)
                                            .memberInfo(memberInfo)
                                            .recommendStatus(RecommendStatus.LIKES)
                                            .build();
            recommendRepository.save(recommend);
            return new RecommendResponse(true, "insert");
        }
    }

    @Transactional
    public RecommendResponse updateThanked(RecommendRequest request) {
        // 익명추천관련해서 추후 구현시 좋아요와 로직 달리할 예정
        Conversation conversation = conversationRepository.findById(request.conId())
                                                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대화입니다."));
        MemberInfo memberInfo = memberInfoRepository.findById(request.userId())
                                                            .orElseThrow(() -> new IllegalArgumentException("로그인이 필요한 서비스입니다."));

        Optional<Recommend> existingRecommend = recommendRepository.findRecommend(conversation, memberInfo, RecommendStatus.THANKED);

        // 선택한 대화에 고마워요가 이미 있을 경우 제거
        if (existingRecommend.isPresent()) {
            recommendRepository.delete(existingRecommend.get());
            return new RecommendResponse(true, "delete");
        }
        // 선택한 대화에 고마워요
        else {
            Recommend recommend = Recommend.builder()
                                            .conversation(conversation)
                                            .memberInfo(memberInfo)
                                            .recommendStatus(RecommendStatus.THANKED)
                                            .build();
            recommendRepository.save(recommend);
            return new RecommendResponse(true, "insert");
        }
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getRecommendedConversations(String token) {
        if (!tokenProvider.isTokenValid(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        Claims claims = tokenProvider.validateToken(token);
        Long memberId = Long.parseLong(claims.getSubject());

        List<Conversation> conversations = recommendRepository.findLikedConversationsByUserId(memberId, RecommendStatus.LIKES);

        return conversations.stream()
                            .map(conversation -> new ConversationResponse(
                                    conversation.getId(),
                                    conversation.getConversationMST().getOwner().getId(),
                                    conversation.getConversationMST().getGuest() != null ? conversation.getConversationMST().getGuest().getId() : null,
                                    conversation.getContent(),
                                    conversation.isGood(),
                                    conversation.isPrivate(),
                                    conversation.isQuestion(),
                                    conversation.getConversationMST().getId()))
            .collect(Collectors.toList());
    }

}
