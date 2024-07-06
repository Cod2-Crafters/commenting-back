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
import jakarta.validation.Valid;
import java.util.Optional;
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
    public SignUpResponse registMember(@Valid SignUpRequest request) {
        chkDupEmail(request.email());
//        SignUpRequest cstRequest = new SignUpRequest(request.email(), "BASE", passwordEncoder.toString());    // 비번암호화 주석처리, 편의를위해 평문으로 등록
        SignUpRequest cstRequest = new SignUpRequest(request.email()
                                                    , request.provider()
                                                    , request.password());
        MemberAuth memberAuth = memberAuthRepository.save(MemberAuth.from(cstRequest));

        MemberInfo memberInfo = MemberInfo.builder()
                                            .email(request.email())
                                            .memberAuth(memberAuth)
                                            .build();
        memberInfoRepository.save(memberInfo);

        try {
            memberAuthRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("중복오류...!");
        }
        return SignUpResponse.from(memberAuth);
    }

    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest request) {
        MemberAuth member = memberAuthRepository.findByEmail(request.email())
                                            .stream()
//                                            .filter(it -> passwordEncoder.matches(request.password(), it.getPassword()))  // 비번암호화 주석처리, 편의를위해 평문으로 등록
                                            .filter(it -> Provider.BASE.equals(request.provider()) ? request.password().equals(it.getPassword()) : true)   // 기본 로그인일 경우만 비번 체크
                                            //.filter(it -> Provider.BASE.equals(request.provider()) ? it.isEnabled() : true)   // 기본 로그인일 경우만 메일인증 체크
                                            .findFirst()
                                            .orElseThrow(() -> new AuthenticationFailedException("아이디 또는 비밀번호가 일치하지 않습니다."));
        String token = tokenProvider.createToken(String.format("%s", member.getId()));
        return new SignInResponse(member.getId(), member.getEmail(), token);
    }

    @Transactional
    public void logout(String token) {
        tokenProvider.invalidateToken(token);
    }

    // 이메일 중복체크
    public boolean chkDupEmail(String email) {
        Optional<MemberAuth> existingMemberAuth = memberAuthRepository.findByEmail(email);
        if (existingMemberAuth.isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }
        return false;
    }

}
