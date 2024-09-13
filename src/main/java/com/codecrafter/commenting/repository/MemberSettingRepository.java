package com.codecrafter.commenting.repository;

import com.codecrafter.commenting.domain.entity.MemberSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSettingRepository extends JpaRepository<MemberSetting, Long> {

}
