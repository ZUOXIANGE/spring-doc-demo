package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户管理", description = "用户的增删改查接口")
@SecurityRequirement(name = "bearer-key")
public class UserController {

    private final AtomicLong idGenerator = new AtomicLong(1000);

    @PostMapping
    @Operation(summary = "创建用户", description = "创建一个新的用户记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "参数校验失败"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        var id = idGenerator.incrementAndGet();
        // 模拟业务逻辑
        return Result.success(new UserResponse(id, request.username(), request.email()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询用户", description = "根据ID查询用户详情")
    public Result<UserResponse> getUser(
            @Parameter(description = "用户ID", example = "1001") 
            @PathVariable Long id) {
        // 模拟查询
        return Result.success(new UserResponse(id, "mock_user", "mock@example.com"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "演示使用 Class + Lombok DTO 进行更新")
    public Result<UserResponse> updateUser(
            @Parameter(description = "用户ID", example = "1001") 
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {
        // 模拟更新逻辑
        return Result.success(new UserResponse(id, "mock_user", request.getEmail()));
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传头像", description = "演示文件上传")
    public Result<String> uploadAvatar(
            @Parameter(description = "用户ID", example = "1001")
            @PathVariable Long id,
            @Parameter(description = "头像文件", required = true) 
            @RequestPart MultipartFile file) {
        return Result.success("File uploaded: " + file.getOriginalFilename());
    }
}
