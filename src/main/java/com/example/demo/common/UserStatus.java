package com.example.demo.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户状态枚举")
public enum UserStatus {
    @Schema(description = "正常")
    NORMAL,
    
    @Schema(description = "锁定")
    LOCKED,
    
    @Schema(description = "禁用")
    DISABLED
}
