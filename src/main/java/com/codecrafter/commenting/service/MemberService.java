package com.codecrafter.commenting.service;

import com.codecrafter.commenting.config.jwt.CustomUserDetails;
import com.codecrafter.commenting.config.jwt.TokenProvider;
import com.codecrafter.commenting.domain.dto.MemberDto;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.domain.entity.base.Provider;
import com.codecrafter.commenting.domain.request.SignInRequest;
import com.codecrafter.commenting.domain.request.SignUpRequest;
import com.codecrafter.commenting.domain.response.SignInResponse;
import com.codecrafter.commenting.domain.response.SignUpResponse;
import com.codecrafter.commenting.exception.AuthenticationFailedException;
import com.codecrafter.commenting.repository.MemberAuthRepository;
import com.codecrafter.commenting.repository.MemberInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {
    private final MemberAuthRepository memberAuthRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public SignUpResponse registMember(SignUpRequest request) {
        SignUpRequest cstRequest = new SignUpRequest(request.email(), "BASE", passwordEncoder.toString());
        MemberAuth memberAuth = memberAuthRepository.save(MemberAuth.from(cstRequest, passwordEncoder));

        MemberInfo memberInfo = MemberInfo.builder()
                                            .email(request.email())
                                            .memberAuth(memberAuth)
                                            .build();
        memberInfoRepository.save(memberInfo);

        try {
            memberAuthRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        return SignUpResponse.from(memberAuth);
    }

    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest request) {
        MemberAuth member = memberAuthRepository.findByEmail(request.email())
                                            .stream()
//                                            .filter(it -> passwordEncoder.matches(request.password(), it.getPassword()))
                                            .findFirst()
                                            .orElseThrow(() -> new AuthenticationFailedException("아이디 또는 비밀번호가 일치하지 않습니다."));
        String token = tokenProvider.createToken(String.format("%s", member.getEmail()));
        return new SignInResponse(member.getEmail(), token);
    }

}
