package com.codecrafter.commenting.repository;

import com.codecrafter.commenting.domain.entity.MemberInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberInfoRepository extends JpaRepository<MemberInfo, Long> {
    Optional<MemberInfo> findByEmail(String email);
    List<MemberInfo> findByCreatedAtBefore(LocalDateTime cutoffDate);

    @Query(
        value = """
            SELECT m.id
            FROM MemberInfo m
            """
    )
    List<Long> findAllIds();
}
