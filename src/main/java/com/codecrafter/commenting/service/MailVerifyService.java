package com.codecrafter.commenting.service;

import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.exception.EmailNotFoundException;
import com.codecrafter.commenting.exception.InvalidCertificationNumberException;
import com.codecrafter.commenting.repository.CertificationNumberRepository;
import com.codecrafter.commenting.repository.MemberAuthRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailVerifyService {

    private final CertificationNumberRepository certificationNumberRepository;
    private final MemberAuthRepository memberAuthRepository;

    public ApiResponse verifyEmail(String email, String certificationNumber) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("email", email);

        if (!isVerify(email, certificationNumber)) {
//            responseData.put("isVerify", false);
            return ApiResponse.error("인증에 실패했습니다.");
        }

        certificationNumberRepository.removeCertificationNumber(email);

        Optional<MemberAuth> optionalMemberAuth = memberAuthRepository.findByEmail(email);
        if (optionalMemberAuth.isEmpty()) {
//            responseData.put("isVerify", false);
            return ApiResponse.error("존재하지 않는 이메일입니다.");
        }

        MemberAuth memberAuth = optionalMemberAuth.get();
        MemberAuth updatedMemberAuth = MemberAuth.builder()
                                                .id(memberAuth.getId())
                                                .email(memberAuth.getEmail())
                                                .provider(memberAuth.getProvider())
                                                .password(memberAuth.getPassword())
                                                .isEnabled(true)    // 인증완료시 계정활성화
                                                .memberInfo(memberAuth.getMemberInfo())
                                                .build();

        memberAuthRepository.save(updatedMemberAuth); // 업데이트된 엔티티를 저장

//        responseData.put("isVerify", true);
        return ApiResponse.success(responseData);
    }

    private boolean isVerify(String email, String certificationNumber) {
        boolean validatedEmail = isEmailExists(email);
//        if (!isEmailExists(email)) {
//            throw new EmailNotFoundException();
//        }
        return (validatedEmail && certificationNumberRepository.getCertificationNumber(email).equals(certificationNumber));
    }

    private boolean isEmailExists(String email) {
        return certificationNumberRepository.hasKey(email);
    }
}
