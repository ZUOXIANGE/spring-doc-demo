package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户响应信息")
public record UserResponse(
    @Schema(description = "用户ID", example = "1001")
    Long id,

    @Schema(description = "用户名", example = "zhangsan")
    String username,

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    String email
) {}
