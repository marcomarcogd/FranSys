package com.fransys.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "成功", data);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(0, "成功", null);
    }

    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>(-1, message, null);
    }
}
