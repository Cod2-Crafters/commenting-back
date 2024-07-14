package com.codecrafter.commenting.domain.dto;

import com.codecrafter.commenting.domain.enumeration.ApiStatus;
import java.io.PrintWriter;
import java.io.StringWriter;

public record ApiResponse(
        ApiStatus status,
        String message,
        Object data,
        String errorlog
) {
    public static ApiResponse success(Object data) {
        return new ApiResponse(ApiStatus.SUCCESS, null, data, null);
    }

    public static ApiResponse error(String message, Throwable throwable) {
        String errorlog = getStackTrace(throwable);
        return new ApiResponse(ApiStatus.ERROR, message, null, errorlog);
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
