package com.example_login_2.business;

import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.RoleUpdateRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.exception.ConflictException;
import com.example_login_2.exception.NotFoundException;
import com.example_login_2.model.User;
import com.example_login_2.service.AdminService;
import com.example_login_2.service.JwtTokenService;
import com.example_login_2.service.StorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Log4j2
@Service
public class AdminBusiness {

    private final AdminService adminService;
    private final StorageService storageService;
    private final JwtTokenService jwtTokenService;

    public AdminBusiness(AdminService adminService, StorageService storageService, JwtTokenService jwtTokenService) {
        this.adminService = adminService;
        this.storageService = storageService;
        this.jwtTokenService = jwtTokenService;
    }

    public List<User> getAllUser() {
        jwtTokenService.validateJwtToken();
        return adminService.getAllUsers();
    }

    public ApiResponse<ModelDTO> getUserById(Long id) {
        jwtTokenService.validateJwtToken();
        User user = adminService.getUserById(id).orElseThrow(NotFoundException::notFound);

        ModelDTO modelDTO = new ModelDTO();
        modelDTO
                .setActivated(String.valueOf(user.getEmailConfirm().isActivated()))
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setPhoneNumber(user.getPhoneNumber())
                .setDateOfBirth(user.getDateOfBirth())
                .setGender(user.getGender())
                .setFileName(user.getFileName())
                .setRole(user.getRoles().toString());

        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<ModelDTO> updateUser(MultipartFile file, UpdateRequest request, Long id) {
        jwtTokenService.validateJwtToken();
        User user = adminService.getUserById(id).orElseThrow(NotFoundException::notFound);

        request.setFileName(storageService.uploadProfilePicture(file));

        user = adminService.updateUserRequest(user, request);

        ModelDTO modelDTO = new ModelDTO()
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setPhoneNumber(user.getPhoneNumber())
                .setDateOfBirth(user.getDateOfBirth())
                .setGender(user.getGender())
                .setFileName(user.getFileName())
                .setRole(user.getRoles().toString());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
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
