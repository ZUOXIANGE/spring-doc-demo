package com.example.demo.config;

import com.example.demo.common.IgnoreResponseWrap;
import com.example.demo.common.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.example.demo.controller")
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public GlobalResponseAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 如果方法或类上有 @IgnoreResponseWrap 注解，则不拦截
        if (returnType.hasMethodAnnotation(IgnoreResponseWrap.class) ||
            returnType.getDeclaringClass().isAnnotationPresent(IgnoreResponseWrap.class)) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        
        // 如果已经是 Result 类型，直接返回
        if (body instanceof Result) {
            return body;
        }

        // 如果是 String 类型，需要手动序列化，因为 StringHttpMessageConverter 会直接写出字符串
        // 而不是将其作为 JSON 对象的一部分
        if (body instanceof String) {
            try {
                // 确保 Content-Type 为 application/json
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(Result.success(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("序列化响应失败", e);
            }
        }

        // 其他类型，统一封装
        return Result.success(body);
    }
}
