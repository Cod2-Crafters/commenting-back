package com.codecrafter.commenting.repository;

import com.codecrafter.commenting.domain.entity.MemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<MemberInfo, Long> {
}
