package com.codecrafter.commenting.repository;

import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.enumeration.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberAuthRepository extends JpaRepository<MemberAuth, Long> {
    Optional<MemberAuth> findByEmailAndProvider(String email, Provider provider);
    Optional<MemberAuth> findByEmail(String email);

    @Query(
        value = """
                SELECT m
                FROM MemberAuth m
                JOIN FETCH m.memberInfo mi
                JOIN FETCH mi.memberSetting
                WHERE m.id = :id
                """
    )
    Optional<MemberAuth> findById(@Param("id") Long id);
}
