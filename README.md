# Spring Boot 4 API 文档最佳实践指南 (Springdoc / OpenAPI 3)

鉴于 Spring Boot 4 (Java 21+) 的生态，本指南推荐使用 **Springdoc (OpenAPI 3)** 作为标准的 API 文档解决方案。它提供了强大的 Swagger UI 交互界面，并且完全符合 OpenAPI 3 规范，易于与 API 网关和客户端代码生成工具集成。

## 1. 核心选型理由

1.  **行业标准**：OpenAPI 3 是 RESTful API 的事实标准，生态极其丰富。
2.  **交互调试**：Swagger UI 提供“试一试”功能，方便开发人员直接在文档页调试接口。
3.  **实时同步**：基于运行时反射生成，修改代码后重启应用即可看到最新文档。
4.  **Spring Boot 原生支持**：通过 Starter 自动配置，开箱即用。

## 2. 环境集成 (Maven)

在 `pom.xml` 中添加 `springdoc-openapi-starter-webmvc-ui` 依赖。

```xml
<dependencies>
    <!-- Springdoc OpenAPI 3 (适配 Spring Boot 3/4) -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.3.0</version> <!-- 请检查最新版本 -->
    </dependency>
</dependencies>
```

## 3. 基础配置

### 3.1 全局元数据配置
创建一个配置类来定义文档的标题、版本和全局信息。

```java
package com.example.marketing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("饮料营销秒杀系统 API")
                        .version("v1.0.0")
                        .description("基于 Spring Boot 4 的高性能秒杀系统 API 文档")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
```

### 3.2 application.yml 配置
在配置文件中微调 Swagger UI 的行为。

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: method
    display-request-duration: true # 显示请求耗时
```

## 4. 编码规范 (Java 21 Record + Annotations)

### 4.1 控制器 (Controller)
使用 `@Tag` 对控制器分组，使用 `@Operation` 描述接口。

```java
package com.example.marketing.controller;

import com.example.marketing.dto.SeckillRequest;
import com.example.marketing.dto.SeckillResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seckill")
@Tag(name = "秒杀管理", description = "秒杀活动的下单与查询接口")
public class SeckillController {

    @PostMapping("/order")
    @Operation(summary = "执行秒杀下单", description = "用户发起秒杀请求，系统检查库存并生成订单。注意：此接口有限流策略。")
    public SeckillResponse createOrder(@RequestBody SeckillRequest request) {
        // 使用 var 简化类型推断
        var orderId = "ORD-" + System.currentTimeMillis();
        return new SeckillResponse(true, orderId, "下单成功");
    }
}
```

### 4.2 数据传输对象 (DTO) - 使用 Java Record
Spring Boot 4 推荐使用 `record`。在 Record 中使用 `@Schema` 注解来描述字段。

> **最佳实践**：直接在 Record 的组件（Compact Constructor 参数位置）上添加注解，最简洁。

```java
package com.example.marketing.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 秒杀请求 DTO
 */
@Schema(description = "秒杀请求参数")
public record SeckillRequest(
    @Schema(description = "用户ID", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    Long userId,

    @Schema(description = "商品SKU ID", example = "SKU-999")
    String skuId,

    @Schema(description = "购买数量", example = "1", defaultValue = "1")
    Integer quantity
) {}
```

### 4.3 响应对象
同样使用 Record 配合 `@Schema`。

```java
package com.example.marketing.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "秒杀响应结果")
public record SeckillResponse(
    @Schema(description = "是否成功", example = "true")
    boolean success,

    @Schema(description = "订单号", example = "ORD-202602021001")
    String orderId,

    @Schema(description = "提示信息", example = "库存扣减成功")
    String message
) {}
```

## 5. 高级技巧

### 5.1 统一响应泛型处理
如果您使用了 `Result<T>` 包装类，Springdoc 会自动解析泛型。

```java
@Schema(description = "统一响应结构")
public record Result<T>(
    @Schema(description = "状态码", example = "200")
    int code,
    
    @Schema(description = "消息", example = "success")
    String message,
    
    T data
) {}
```

### 5.2 接口分组
如果 API 较多，可以创建多个分组（例如：前台 API、后台管理 API）。

```java
@Bean
public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
            .group("public-api")
            .pathsToMatch("/api/v1/**")
            .build();
}

@Bean
public GroupedOpenApi adminApi() {
    return GroupedOpenApi.builder()
            .group("admin-api")
            .pathsToMatch("/admin/**")
            .build();
}
```

## 6. 访问文档

启动 Spring Boot 应用后，访问以下地址：

*   **HTML 文档 (Swagger UI)**: `http://localhost:8080/swagger-ui.html`
*   **JSON 元数据**: `http://localhost:8080/v3/api-docs`
