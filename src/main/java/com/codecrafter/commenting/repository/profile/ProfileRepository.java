package com.codecrafter.commenting.repository.profile;

import com.codecrafter.commenting.domain.entity.MemberInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProfileRepository extends JpaRepository<MemberInfo, Long>, ProfileRepositoryCustom {

}
