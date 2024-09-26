package com.example_login_2.controller_unit_test;

import com.example_login_2.business.AdminBusiness;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthResponse.MUserResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.api.AdminController;
import com.example_login_2.controller.request.RoleUpdateRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.mapper.UserMapper;
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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@ContextConfiguration เพื่อให้แน่ใจว่า Spring จะสร้างและจัดการ bean ที่จำเป็นทั้งหมดตามที่ต้องการในบริบทของการทดสอบ
@ContextConfiguration(classes = {AdminController.class, AdminControllerTest.TestSecurityConfig.class})
@WebMvcTest(AdminController.class)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AdminBusiness business;
    @MockBean
    private UserMapper userMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ModelDTO mockModelDTO;
    private ApiResponse<ModelDTO> mockResponse;

    private MUserResponse mockMapper;
    private ApiResponse<MUserResponse> mockResponseMapper;

    @BeforeEach
    public void setUp() {
        mockModelDTO = new ModelDTO();
        mockModelDTO.setFirstName("test");
        mockModelDTO.setLastName("test");

        mockResponse = new ApiResponse<>(true, TestData.message, mockModelDTO);

        mockResponseMapper = new ApiResponse<>(true, TestData.message, mockMapper);

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
    public void testGetUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        List<User> users = Arrays.asList(user1, user2);

        List<MUserResponse> userResponseList = userMapper.toUserResponseList(users);

        when(business.getAllUser()).thenReturn(userResponseList);

        mockMvc.perform(get("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(business, times(1)).getAllUser();

    }

    @Test
    public void testGetUserById_Success() throws Exception {
        when(business.getUserById(anyLong())).thenReturn(mockResponseMapper);

        mockMvc.perform(get("/api/v1/admin/{id}", TestData.id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestData.message));
//                .andExpect(jsonPath("$.data").exists());

        verify(business, times(1)).getUserById(eq(TestData.id));
    }

    @Test
    public void testPutUser() throws Exception {
        long userId = TestData.id;
        UpdateRequest updateRequest = new UpdateRequest();

        when(business.updateUser(any(UpdateRequest.class), anyLong())).thenReturn(mockResponseMapper);

        mockMvc.perform(put("/api/v1/admin/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestData.message));
//                .andExpect(jsonPath("$.data").exists());

        verify(business, times(1)).updateUser(any(UpdateRequest.class), eq(userId));
    }

    @Test
    public void testRemoveUserRole() throws Exception {
        long userId = TestData.id;
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRole("ROLE_ADMIN");

        when(business.removeUserRole(any(RoleUpdateRequest.class), anyLong())).thenReturn(mockResponse);

        mockMvc.perform(put("/api/v1/admin/removeRole/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(TestData.message))
                .andExpect(jsonPath("$.data").exists());

        verify(business, times(1)).removeUserRole(any(RoleUpdateRequest.class), eq(userId));
    }

    @Test
    public void testDeleteUser() throws Exception {
        long userId = TestData.id;

        doNothing().when(business).deleteUser(anyLong());

        mockMvc.perform(delete("/api/v1/admin/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(business, times(1)).deleteUser(eq(userId));
    }

    @Test
    public void testSearchByRole() throws Exception {
        String role = "ROLE_ADMIN";
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        List<User> users = Arrays.asList(user1, user2);

        when(business.searchRoleUser(anyString())).thenReturn(users);

        mockMvc.perform(get("/api/v1/admin/search")
                        .param("role", role)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(business, times(1)).searchRoleUser(eq(role));

    }

    interface TestData {
        long id = 1L;
        String message = "Operation completed successfully";
    }
}
