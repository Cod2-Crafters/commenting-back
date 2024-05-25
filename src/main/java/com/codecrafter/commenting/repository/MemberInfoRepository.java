package com.codecrafter.commenting.repository;

import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberInfoRepository extends JpaRepository<MemberInfo, Long> {
    Optional<MemberInfo> findByEmail(String email);

}
