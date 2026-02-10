package com.example.demo.config;

import com.example.demo.common.IgnoreResponseWrap;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .info(new Info()
                        .title("用户管理系统 API")
                        .version("v1.0.0")
                        .description("基于 Spring Boot 4 + Springdoc 的示例项目")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .pathsToMatch("/api/**")
                .build();
    }
    
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin-api")
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            if (handlerMethod.hasMethodAnnotation(IgnoreResponseWrap.class) ||
                handlerMethod.getBeanType().isAnnotationPresent(IgnoreResponseWrap.class)) {
                return operation;
            }

            operation.getResponses().forEach((responseCode, apiResponse) -> {
                if ("200".equals(responseCode) && apiResponse.getContent() != null) {
                    apiResponse.getContent().forEach((mediaTypeKey, mediaType) -> {
                        Schema<?> originalSchema = mediaType.getSchema();
                        if (originalSchema != null) {
                            Schema<?> wrappedSchema = new Schema<>();
                            wrappedSchema.setType("object");
                            wrappedSchema.addProperty("code", new IntegerSchema().example(200).description("状态码"));
                            wrappedSchema.addProperty("message", new StringSchema().example("success").description("消息"));
                            wrappedSchema.addProperty("data", originalSchema);
                            wrappedSchema.setDescription("统一响应结构");
                            
                            mediaType.setSchema(wrappedSchema);
                        }
                    });
                }
            });

            return operation;
        };
    }
}
