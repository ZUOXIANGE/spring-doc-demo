package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testCreateUser_ShouldBeWrappedInResult() throws Exception {
        String requestBody = """
                {
                    "username": "test_user",
                    "email": "test@example.com",
                    "age": 25,
                    "status": "NORMAL"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.username").value("test_user"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    public void testGetUser_ShouldBeWrappedInResult() throws Exception {
        mockMvc.perform(get("/api/v1/users/1001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.username").value("mock_user"));
    }

    @Test
    public void testUpdateUser_ShouldBeWrappedInResult() throws Exception {
        String requestBody = """
                {
                    "email": "updated@example.com",
                    "age": 30
                }
                """;

        mockMvc.perform(put("/api/v1/users/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"));
    }

    @Test
    public void testUploadAvatar_ShouldReturnWrappedString() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                "test content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/users/1001/avatar").file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("File uploaded: avatar.png"));
    }

    @Test
    public void testPing_ShouldNotBeWrapped() throws Exception {
        mockMvc.perform(get("/api/v1/users/ping"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    @Test
    public void testDownloadFile_ShouldNotBeWrapped() throws Exception {
        mockMvc.perform(get("/api/v1/users/download"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Hello, this is a downloadable file content."));
    }

    @Test
    public void testBizException_ShouldReturnErrorResult() throws Exception {
        mockMvc.perform(get("/api/v1/users/error"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(50001))
                .andExpect(jsonPath("$.message").value("演示业务异常"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testValidationException_ShouldReturnErrorResult() throws Exception {
        String requestBody = """
                {
                    "username": "",
                    "email": "invalid-email",
                    "age": 150,
                    "status": "NORMAL"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testHttpMessageNotReadable_ShouldReturnErrorResult() throws Exception {
        String requestBody = """
                {
                    "username": "test",
                    "status": "INVALID_ENUM"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("请求体格式错误或类型不匹配"));
    }
}
