package com.codecrafter.commenting.service;

import com.codecrafter.commenting.domain.dto.MemberDto;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.entity.base.Provider;
import com.codecrafter.commenting.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationIdStr = userRequest.getClientRegistration().getRegistrationId();
        Provider registrationId = Provider.valueOf(registrationIdStr.toUpperCase());

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        MemberDto member = Provider.extract(registrationId, attributes);

        member.setProvider(registrationId);
        MemberAuth memberAuth = saveOrUpdate(member);

        Map<String, Object> customAttribute = customAttribute(attributes, userNameAttributeName, member, registrationId);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                customAttribute,
                userNameAttributeName);

    }

    private Map customAttribute(Map attributes, String userNameAttributeName, MemberDto member, Provider registrationId) {
        Map<String, Object> customAttribute = new LinkedHashMap<>();
        customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttribute.put("provider", registrationId);
        customAttribute.put("email", member.getEmail());
        return customAttribute;

    }

    private MemberAuth saveOrUpdate(MemberDto member) {

        MemberAuth memberAuth = memberRepository.findByEmailAndProvider(member.getEmail(), member.getProvider())
                .map(m -> m.update(member.getEmail()))
                .orElse(member.toMember());

        return memberRepository.save(memberAuth);
    }


}

