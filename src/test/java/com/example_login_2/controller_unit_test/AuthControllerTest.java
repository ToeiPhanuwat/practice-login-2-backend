package com.example_login_2.controller_unit_test;

import com.example_login_2.business.AuthBusiness;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthRequest.*;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.api.AuthController;
import com.example_login_2.controller.request.UpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {AuthController.class, AuthControllerTest.TestSecurityConfig.class})
@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthBusiness business;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ModelDTO mockModelDTO;
    private ApiResponse<ModelDTO> mockResponse;
    @BeforeEach
    public void setUp() {
        mockModelDTO = new ModelDTO();
        mockModelDTO.setFirstName("test");
        mockModelDTO.setLastName("test");

        mockResponse = new ApiResponse<>(true, TestDate.message, mockModelDTO);
    }

    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    public void testRegister() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");

        when(business.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/auth/registers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message))
                .andExpect(jsonPath("$.data").exists());

        verify(business, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    public void testLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");

        when(business.login(any(LoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message))
                .andExpect(jsonPath("$.data").exists());

        verify(business, times(1)).login(any(LoginRequest.class));
    }

    @Test
    public void testActivate() throws Exception {
        ActivateRequest request = new ActivateRequest();
        request.setToken("test-token-activate");

        when(business.activate(any(ActivateRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/auth/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message))
                .andExpect(jsonPath("$.data").exists());

        verify(business, times(1)).activate(any(ActivateRequest.class));
    }

    @Test
    public void testLogout() throws Exception {
        ApiResponse<String> response = new ApiResponse<>(true, TestDate.message, null);

        when(business.logout()).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message));
        verify(business).logout();
    }

    @Test
    public void testResendActivationEmail() throws Exception {
        ResendActivationEmailRequest request = new ResendActivationEmailRequest();
        request.setToken("test-resend-activation-email");

        ApiResponse<String> response = new ApiResponse<>(true, TestDate.message, null);

        when(business.resendActivationEmail(any(ResendActivationEmailRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/resend-activation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message));

        verify(business).resendActivationEmail(any(ResendActivationEmailRequest.class));
    }

    @Test
    public void testForgotPassword() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@test.com");

        when(business.forgotPassword(any(ForgotPasswordRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message))
                .andExpect(jsonPath("$.data").exists());

        verify(business).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    public void testResetPassword() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setToken("test-token-reset-password");
        request.setNewPassword("test-new-password");

        ApiResponse<String> response = new ApiResponse<>(true, TestDate.message, null);
        when(business.resetPassword(any(PasswordResetRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message));

        verify(business).resetPassword(any(PasswordResetRequest.class));
    }

    @Test
    public void testGetUserInfo() throws Exception {
        when(business.getUserById()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/auth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message))
                .andExpect(jsonPath("$.data").exists());

        verify(business).getUserById();
    }

    @Test
    public void testPutUser() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setFileName("test.png");
        MockMultipartFile mockRequest = new MockMultipartFile(
                "request",
                "request.json",
                "application/json",
                objectMapper.writeValueAsString(updateRequest).getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "Test content".getBytes(StandardCharsets.UTF_8)
        );

        when(business.updateUser(any(MultipartFile.class), any(UpdateRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/auth")
                .file(mockFile)
                .file(mockRequest)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message))
                .andExpect(jsonPath("$.data").exists());

        verify(business).updateUser(any(MultipartFile.class), any(UpdateRequest.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(business).deleteUser();

        mockMvc.perform(delete("/api/v1/auth"))
                .andExpect(status().isNoContent());

        verify(business).deleteUser();
    }

    @Test
    public void testRefreshToken() throws Exception {
        when(business.refreshJwtToken()).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/auth/refresh-token"))
                .andExpect(status().isOk());

        verify(business).refreshJwtToken();
    }

    interface TestDate {
        String message = "Operation completed successfully";
    }
}