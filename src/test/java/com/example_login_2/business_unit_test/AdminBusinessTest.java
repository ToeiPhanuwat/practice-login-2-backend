package com.example_login_2.business_unit_test;

import com.example_login_2.business.AdminBusiness;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthResponse.MUserResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.RoleUpdateRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.exception.ConflictException;
import com.example_login_2.exception.NotFoundException;
import com.example_login_2.model.EmailConfirm;
import com.example_login_2.model.User;
import com.example_login_2.service.AdminService;
import com.example_login_2.service.JwtTokenService;
import com.example_login_2.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminBusinessTest {
    @Mock
    private AdminService adminService;
    @Mock
    private StorageService storageService;
    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AdminBusiness business;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(TestData.id);
        mockUser.setEmail(TestData.email);
        mockUser.setPassword(TestData.password);
        mockUser.setRoles(new HashSet<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN")));
    }

    @Test
    public void testGetAllUser() {
        User user1 = new User()
                .setEmail(TestData.email)
                .setPassword(TestData.password);
        User user2 = new User()
                .setEmail(TestData.email)
                .setPassword(TestData.password);
        List<User> mock = Arrays.asList(user1, user2);

        doNothing().when(jwtTokenService).validateJwtToken();
        when(adminService.getAllUsers()).thenReturn(mock);

        List<MUserResponse> result = business.getAllUser();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TestData.email, result.get(0).getEmail());
        assertEquals(TestData.email, result.get(1).getEmail());

        verify(jwtTokenService).validateJwtToken();
        verify(adminService).getAllUsers();
    }

    @Test
    public void testGetUserById_Success() {
        EmailConfirm emailConfirm = new EmailConfirm()
                .setActivated(true);
        mockUser.setEmailConfirm(emailConfirm);

        doNothing().when(jwtTokenService).validateJwtToken();
        when(adminService.getUserById(anyLong())).thenReturn(Optional.of(mockUser));

        ApiResponse<MUserResponse> response = business.getUserById(TestData.id);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertNotNull(response.getData().getAddress());

        verify(jwtTokenService).validateJwtToken();
        verify(adminService).getUserById(anyLong());
    }

    @Test
    public void testGetUserById_UserNotFound() {
        doNothing().when(jwtTokenService).validateJwtToken();
        when(adminService.getUserById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> business.getUserById(TestData.id));

        verify(jwtTokenService).validateJwtToken();
        verify(adminService).getUserById(anyLong());
    }

    @Test
    public void testUpdateUser_Success() {
        UpdateRequest request = new UpdateRequest();

        doNothing().when(jwtTokenService).validateJwtToken();
        when(adminService.getUserById(anyLong())).thenReturn(Optional.of(mockUser));
        when(adminService.updateUserRequest(any(User.class), any(UpdateRequest.class))).thenReturn(mockUser);

        ApiResponse<MUserResponse> response = business.updateUser(request, TestData.id);

        assertNotNull(response);
        assertNotNull(response.getData());

        verify(jwtTokenService).validateJwtToken();
        verify(adminService).getUserById(anyLong());
        verify(adminService).updateUserRequest(any(User.class), any(UpdateRequest.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        UpdateRequest request = new UpdateRequest();

        doNothing().when(jwtTokenService).validateJwtToken();
        when(adminService.getUserById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> business.updateUser(request, TestData.id));

        verify(jwtTokenService).validateJwtToken();
        verify(adminService).getUserById(anyLong());
    }

    @Test
    public void testRemoveUserRole_Success() {
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRole("ROLE_ADMIN");

        doNothing().when(jwtTokenService).validateJwtToken();
        when(adminService.getUserById(anyLong())).thenReturn(Optional.of(mockUser));
        when(adminService.removeRoleAndUpdate(any(User.class), any(RoleUpdateRequest.class))).thenReturn(mockUser);

        ApiResponse<ModelDTO> response = business.removeUserRole(request, TestData.id);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertNotNull(response.getData().getRole());
        assertTrue(response.getData().getRole().contains("ROLE_USER"));

        verify(jwtTokenService).validateJwtToken();
        verify(adminService).getUserById(anyLong());
        verify(adminService).removeRoleAndUpdate(any(User.class), any(RoleUpdateRequest.class));
    }

    @Test
    public void testRemoveUserRole_UserNotFound() {
        RoleUpdateRequest request = new RoleUpdateRequest();

        doNothing().when(jwtTokenService).validateJwtToken();
        when(adminService.getUserById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> business.removeUserRole(request, TestData.id));

        verify(jwtTokenService).validateJwtToken();
        verify(adminService).getUserById(anyLong());
    }

    @Test
    public void testRemoveUserRole_UserHasOneRole() {
        RoleUpdateRequest request = new RoleUpdateRequest();
        User user = new User()
                .setRoles(new HashSet<>(List.of("ROLE_USER")));

        doNothing().when(jwtTokenService).validateJwtToken();
        when(adminService.getUserById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> business.removeUserRole(request, TestData.id));

        verify(jwtTokenService).validateJwtToken();
        verify(adminService).getUserById(anyLong());
    }

    @Test
    public void testDeleteUser_Success() {
        doNothing().when(jwtTokenService).validateJwtToken();
        when(adminService.getUserById(anyLong())).thenReturn(Optional.of(mockUser));
        doNothing().when(adminService).deleteUser(anyLong());

        business.deleteUser(TestData.id);

        verify(jwtTokenService).validateJwtToken();
        verify(adminService).getUserById(anyLong());
        verify(adminService).deleteUser(anyLong());
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        doNothing().when(jwtTokenService).validateJwtToken();
        when(adminService.getUserById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> business.deleteUser(TestData.id));

        verify(jwtTokenService).validateJwtToken();
        verify(adminService).getUserById(anyLong());
    }

    @Test
    public void testSearchRoleUser() {
        String role = "ROLE_USER";
        User user1 = new User()
                .setRoles(new HashSet<>(List.of("ROLE_USER", "ROLE_ADMIN")));
        User user2 = new User()
                .setRoles(new HashSet<>(List.of("ROLE_USER")));
        List<User> users = Arrays.asList(user1, user2);

        when(adminService.searchRoleUser(anyString())).thenReturn(users);

        List<User> search = adminService.searchRoleUser(role);

        assertNotNull(search);
        assertEquals(2, search.size());
        assertEquals(user2.getRoles(), search.get(1).getRoles());

        verify(adminService).searchRoleUser(anyString());
    }

    interface TestData {
        Long id = 1L;
        String email = "test@email.com";

        String password = "password";
    }
}




