package com.example_login_2.controller_unit_test;

import com.example_login_2.business.AuthBusiness;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthRequest.ForgotPasswordRequest;
import com.example_login_2.controller.AuthRequest.LoginRequest;
import com.example_login_2.controller.AuthRequest.PasswordResetRequest;
import com.example_login_2.controller.AuthRequest.RegisterRequest;
import com.example_login_2.controller.AuthResponse.MUserResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.api.AuthController;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {AuthController.class, AuthControllerTest.TestSecurityConfig.class})
@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthBusiness business;
    private ModelDTO mockModelDTO;
    private ApiResponse<ModelDTO> mockResponse;
    private User mockUse;
    private ApiResponse<User> mockResponseUser;
    private MUserResponse mockMapper;
    private ApiResponse<MUserResponse> mockResponseMapper;

    @BeforeEach
    public void setUp() {
        mockModelDTO = new ModelDTO();
        mockModelDTO.setFirstName("test");
        mockModelDTO.setLastName("test");

        mockResponse = new ApiResponse<>(true, TestDate.message, mockModelDTO);

        mockUse = new User();
        mockUse.setFirstName("test");
        mockUse.setEmail("test@gmail.com");
        mockUse.setPassword("test");

        mockResponseUser = new ApiResponse<>(true, TestDate.message, mockUse);

        mockResponseMapper = new ApiResponse<>(true, TestDate.message, mockMapper);
    }

    @Test
    public void testRegister() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("test");
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
        when(business.activate(anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/auth/activate/{token}", TestDate.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message))
                .andExpect(jsonPath("$.data").exists());

        verify(business, times(1)).activate(anyString());
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
        ApiResponse<String> response = new ApiResponse<>(true, TestDate.message, null);

        when(business.resendActivationEmail(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/v1/auth/resend-activation-email/{token}", TestDate.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message));

        verify(business).resendActivationEmail(anyString());
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
        when(business.getUserById()).thenReturn(mockResponseMapper);

        mockMvc.perform(get("/api/v1/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message));
//                .andExpect(jsonPath("$.data").exists());

        verify(business).getUserById();
    }

    @Test
    public void testPutUser() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest();

        when(business.updateUser(any(UpdateRequest.class))).thenReturn(mockResponseMapper);

        mockMvc.perform(put("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestDate.message));
//                .andExpect(jsonPath("$.data").exists());

        verify(business).updateUser(any(UpdateRequest.class));
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
        String token = "token";
        String message = "Operation completed successfully";
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
}
