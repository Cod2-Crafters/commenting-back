package com.codecrafter.commenting.controller.interceptor;

import static com.codecrafter.commenting.util.ResponseUtil.createResponseBody;

import com.codecrafter.commenting.config.SecurityUtil;
import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.enumeration.ApiStatus;
import com.codecrafter.commenting.repository.conversation.ConversationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@RequiredArgsConstructor
public class RecommendInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;
    private final ConversationRepository conversationRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long currentMemberId = SecurityUtil.getCurrentMember().getId();
        // currentMemberId == 0 --> 로그인 안한것 --> 권한 없다.
        if (currentMemberId == 0L) {
            createResponseBody(
                response,
                new ApiResponse(ApiStatus.ERROR,"로그인이 필요한 서비스입니다.", null, null),
                HttpStatus.FORBIDDEN
            );
            return false;
        }

        String requestMethod = request.getMethod();

        if (requestMethod.equals("POST")) {
            String json = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(json);

            Long conId = jsonNode.get("conId").asLong();
            Long userId = jsonNode.get("userId").asLong();

            Conversation conversation = conversationRepository.findById(conId)
                                                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대화입니다."));
            boolean isQuestion = conversation.isQuestion();

            AntPathMatcher pathMatcher = new AntPathMatcher();
            String requestUri = request.getRequestURI();

            // 좋아요일때
            if (pathMatcher.match("/api/recommends/likes", requestUri)) {
                if (isQuestion) {
                    //정상적으로 좋아요 실행
                    return true;
                }

                createResponseBody(
                    response,
                    new ApiResponse(ApiStatus.ERROR,"답변에 좋아요를 누를 수 없습니다.", null, null),
                    HttpStatus.BAD_REQUEST
                );
                return false;
            }

            // 고마워요일때
            if (pathMatcher.match("/api/recommends/thanked", requestUri)) {
                if (!isQuestion) {
                    //정상적으로 고마워요 실행
                    return true;
                }

                createResponseBody(
                    response,
                    new ApiResponse(ApiStatus.ERROR,"질문에 고마워요를 누를 수 없습니다.", null, null),
                    HttpStatus.BAD_REQUEST
                );
                return false;
            }
        }

        if (requestMethod.equals("GET")) {
            return true;
        }

        return false;
    }
}
