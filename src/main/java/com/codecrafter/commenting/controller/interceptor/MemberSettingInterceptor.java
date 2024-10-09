package com.codecrafter.commenting.controller.interceptor;

import static com.codecrafter.commenting.util.ResponseUtil.createResponseBody;

import com.codecrafter.commenting.config.SecurityUtil;

import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.enumeration.ApiStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class MemberSettingInterceptor implements HandlerInterceptor {

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
        return true;
    }


}
