package com.example_login_2.business;

import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthResponse.MUserResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.RoleUpdateRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.exception.ConflictException;
import com.example_login_2.exception.NotFoundException;
import com.example_login_2.mapper.UserMapper;
import com.example_login_2.model.User;
import com.example_login_2.service.AdminService;
import com.example_login_2.service.JwtTokenService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class AdminBusiness {

    private final AdminService adminService;
    private final JwtTokenService jwtTokenService;
    private final UserMapper userMapper;

    public AdminBusiness(AdminService adminService, JwtTokenService jwtTokenService, UserMapper userMapper) {
        this.adminService = adminService;
        this.jwtTokenService = jwtTokenService;
        this.userMapper = userMapper;
    }

    public List<MUserResponse> getAllUser() {
        jwtTokenService.validateJwtToken();

        List<User> users = adminService.getAllUsers();
        log.info("Fetched all user data successfully.");

        return userMapper.toUserResponseList(users);
    }

    public ApiResponse<MUserResponse> getUserById(Long id) {
        jwtTokenService.validateJwtToken();
        User user = adminService.getUserById(id).orElseThrow(() -> {
            log.warn("๊User with ID: {} not found.", id);
            return NotFoundException.notFound();
        });

        MUserResponse mUserResponse = userMapper.toUserResponse(user);
        log.info("Fetched user profile for user ID: {}.", id);

        return new ApiResponse<>(true, "Operation completed successfully", mUserResponse);
    }

    public ApiResponse<MUserResponse> updateUser(UpdateRequest request, Long id) {
        jwtTokenService.validateJwtToken();
        log.info("Updating user profile with request.");
        User user = adminService.getUserById(id).orElseThrow(() -> {
            log.warn("User with ID: {} not found.", id);
            return NotFoundException.tokenNotFound();
        });
        user = adminService.updateUserRequest(user, request);

        MUserResponse mUserResponse = userMapper.toUserResponse(user);
        log.info("User profile updated successfully for user ID: {}.", id);

        return new ApiResponse<>(true, "Operation completed successfully", mUserResponse);
    }

    public ApiResponse<ModelDTO> removeUserRole(RoleUpdateRequest role, Long id) {
        jwtTokenService.validateJwtToken();
        log.info("Deleting a user role by request.");
        User user = adminService.getUserById(id).orElseThrow(() -> {
            log.warn("User with ID: {} not found.", id);
            return NotFoundException.tokenNotFound();
        });
        if (user.getRoles().size() < 2) throw ConflictException.userHasOneRole();
        user = adminService.removeRoleAndUpdate(user, role);
        log.info("Deleted user role successfully for user ID : {}.", id);

        ModelDTO modelDTO = new ModelDTO()
                .setRole(user.getRoles().toString());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public void deleteUser(Long id) {
        jwtTokenService.validateJwtToken();
        log.info("Deleting users on request.");
        User user = adminService.getUserById(id).orElseThrow(() -> {
            log.warn("User with ID: {} not found.", id);
            return NotFoundException.tokenNotFound();
        });
        adminService.deleteUser(id);
        log.info("Deleted user successfully for user ID: {}.", id);
    }

    public List<User> searchRoleUser(String role) {
        return adminService.searchRoleUser(role);
    }

}
