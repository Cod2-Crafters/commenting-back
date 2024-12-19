package com.codecrafter.commenting.repository.profile;

import com.codecrafter.commenting.domain.entity.MemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<MemberInfo, Long>, ProfileRepositoryCustom {

}
