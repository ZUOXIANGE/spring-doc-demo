package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户更新请求 (Lombok Class示例)")
public class UserUpdateRequest {

    @Schema(description = "邮箱", example = "new_email@example.com")
    private String email;

    @Schema(description = "年龄", example = "20", minimum = "0", maximum = "150")
    private Integer age;
}
