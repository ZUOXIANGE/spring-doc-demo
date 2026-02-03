package com.example.demo.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "统一响应结构")
public record Result<T>(
    @Schema(description = "状态码", example = "200")
    int code,

    @Schema(description = "消息", example = "success")
    String message,

    @Schema(description = "业务数据")
    T data
) {
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}
