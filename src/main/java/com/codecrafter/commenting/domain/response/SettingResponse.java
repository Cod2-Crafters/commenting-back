package com.codecrafter.commenting.domain.response;

import com.codecrafter.commenting.domain.enumeration.SettingName;

public record SettingResponse(
    SettingName name,
    Boolean status
) {

}
