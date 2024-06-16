package com.codecrafter.commenting.config.filter;

import com.codecrafter.commenting.config.jwt.JwtUtil;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.repository.MemberAuthRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final MemberAuthRepository memberAuthRepository;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;
        log.info("doFilterInternal.requestTokenHeader : {}", requestTokenHeader);
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                log.info("doFilterInternal.jwtToken : {}", jwtToken);
                username = jwtUtil.getUserIdFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token", e);
            } catch (ExpiredJwtException e) {
                log.error("JWT Token has expired", e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<MemberAuth> memberAuthOptional = memberAuthRepository.findByEmail(username);
            if (memberAuthOptional.isPresent() && jwtUtil.validateToken(jwtToken) != null) {
                MemberAuth memberAuth = memberAuthOptional.get();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    memberAuth, null, memberAuth.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

}
