package com.codecrafter.commenting.config;

import com.codecrafter.commenting.domain.entity.MemberAuth;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static MemberAuth getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MemberAuth) {
            MemberAuth memberAuth = (MemberAuth) authentication.getPrincipal();
            return memberAuth;
        }
        return MemberAuth.builder().id(0L).build();
    }
}

