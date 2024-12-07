package com.codecrafter.commenting.controller.interceptor;

import static com.codecrafter.commenting.util.ResponseUtil.createResponseBody;

import com.codecrafter.commenting.config.SecurityUtil;
import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.entity.MemberSetting;
import com.codecrafter.commenting.domain.enumeration.ApiStatus;
import com.codecrafter.commenting.domain.request.conversation.CreateConversationRequest;
import com.codecrafter.commenting.repository.MemberSettingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;


@RequiredArgsConstructor
@Component
public class ConversationQuestionInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;
    private final MemberSettingRepository memberSettingRepository;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String json = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8); // 바디 캐싱
        CreateConversationRequest createConversationRequest = objectMapper.readValue(json, CreateConversationRequest.class);
        Long ownerId = createConversationRequest.ownerId();

        if (ownerId == null) {
            createResponseBody(
                response,
                new ApiResponse(ApiStatus.ERROR, "질문 작성 대상을 지정하지 않았습니다.", null, null),
                HttpStatus.FORBIDDEN
            );
            return false;
        }

        MemberSetting memberSetting = memberSettingRepository.findById(ownerId).orElseThrow();
        Long currentMemberId = SecurityUtil.getCurrentMember().getId();

        // 1. 받는 사람이 익명(비로그인)질문 받니?
        if (!memberSetting.getAllowAnonymous() && currentMemberId.equals(0L)) { // 안받고 + 비로그인
            createResponseBody(
                response,
                new ApiResponse(ApiStatus.ERROR, "비로그인 질문을 받지 않는 회원입니다.", null, null),
                HttpStatus.FORBIDDEN
            );
            return false;
        }

        // 2. 받는 사람이 스페이스 일시정지 상태니?
        if (memberSetting.getIsSpacePaused()) {
            createResponseBody(
                response,
                new ApiResponse(ApiStatus.ERROR, "스페이스 일시정지 회원입니다.", null, null),
                HttpStatus.FORBIDDEN
            );
            return false;
        }

        return true;
    }

}
