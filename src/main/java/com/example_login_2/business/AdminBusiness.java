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

        return userMapper.toUserResponseList(users);
    }

    public ApiResponse<MUserResponse> getUserById(Long id) {
        jwtTokenService.validateJwtToken();
        User user = adminService.getUserById(id).orElseThrow(NotFoundException::notFound);

        MUserResponse mUserResponse = userMapper.toUserResponse(user);

        return new ApiResponse<>(true, "Operation completed successfully", mUserResponse);
    }

    public ApiResponse<MUserResponse> updateUser(UpdateRequest request, Long id) {
        jwtTokenService.validateJwtToken();
        User user = adminService.getUserById(id).orElseThrow(NotFoundException::notFound);
        user = adminService.updateUserRequest(user, request);

        MUserResponse mUserResponse = userMapper.toUserResponse(user);

        return new ApiResponse<>(true, "Operation completed successfully", mUserResponse);
    }

    public ApiResponse<ModelDTO> removeUserRole(RoleUpdateRequest role, Long id) {
        jwtTokenService.validateJwtToken();
        User user = adminService.getUserById(id).orElseThrow(NotFoundException::notFound);
        if (user.getRoles().size() < 2) throw ConflictException.userHasOneRole();
        user = adminService.removeRoleAndUpdate(user, role);

        ModelDTO modelDTO = new ModelDTO()
                .setRole(user.getRoles().toString());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public void deleteUser(Long id) {
        jwtTokenService.validateJwtToken();
        User user = adminService.getUserById(id).orElseThrow(NotFoundException::notFound);
        adminService.deleteUser(id);
    }

    public List<User> searchRoleUser(String role) {
        return adminService.searchRoleUser(role);
    }

}
