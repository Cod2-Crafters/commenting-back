package com.codecrafter.commenting.repository.profile;

import com.codecrafter.commenting.domain.dto.MemberInfoDto;

public interface ProfileRepositoryCustom {
    MemberInfoDto getProfileResponse(Long memberId);
}
