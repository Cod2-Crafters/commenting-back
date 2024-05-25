package com.codecrafter.commenting.repository;

import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.entity.base.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberAuthRepository extends JpaRepository<MemberAuth, Long> {
    Optional<MemberAuth> findByEmailAndProvider(String email, Provider provider);
}
