package com.codecrafter.commenting.domain.response;

import lombok.Builder;

@Builder
public record SettingDataResponse(
    Boolean allowAnonymous,
    Boolean emailNotice,
    Boolean allowGlobalQuestion,
    Boolean isSpacePaused
) {

}
