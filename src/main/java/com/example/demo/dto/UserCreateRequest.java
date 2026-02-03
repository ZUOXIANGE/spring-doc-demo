package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户创建请求")
public record UserCreateRequest(
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    String username,

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    String email,

    @Schema(description = "年龄", example = "18", minimum = "0", maximum = "150")
    Integer age
) {}
