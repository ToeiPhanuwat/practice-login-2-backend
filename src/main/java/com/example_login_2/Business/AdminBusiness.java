package com.example_login_2.Business;

import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.RoleUpdateRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.exception.ConflictException;
import com.example_login_2.exception.NotFoundException;
import com.example_login_2.model.Address;
import com.example_login_2.model.User;
import com.example_login_2.service.AddressService;
import com.example_login_2.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminBusiness {

    private final AdminService adminService;
    private final AddressService addressService;

    public AdminBusiness(AdminService adminService, AddressService addressService) {
        this.adminService = adminService;
        this.addressService = addressService;
    }

    public List<User> getAllUser() { //TODO: BUC response ออกมาต่อๆกัน
        return adminService.getAllUsers();
    }

    public ApiResponse<ModelDTO> getUserById(Long id) {
        User user = adminService.getUserById(id).orElseThrow(NotFoundException::notFound);
        Address address = user.getAddress();

        ModelDTO modelDTO = new ModelDTO()
                .setActivated(String.valueOf(user.getEmailConfirm().isActivated()))
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setPhoneNumber(user.getPhoneNumber())
                .setDateOfBirth(user.getDateOfBirth())
                .setGender(user.getGender())
                .setRole(user.getRoles().toString())
                .setAddress(address.getAddress())
                .setCity(address.getCity())
                .setStateProvince(address.getStateProvince())
                .setPostalCode(address.getPostalCode())
                .setCountry(address.getCountry());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<ModelDTO> updateUser(UpdateRequest request, Long id) {
        User user = adminService.getUserById(id).orElseThrow(NotFoundException::notFound);
        user = adminService.updateUserRequest(user, request);

        Address address = user.getAddress();
        if (address == null) {
            address = addressService.createAddress(user, request);
        } else {
            address = addressService.updateAddressUser(user, address, request);
        }
        adminService.updateAddress(user, address);

        ModelDTO modelDTO = new ModelDTO()
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setPhoneNumber(request.getPhoneNumber())
                .setDateOfBirth(request.getDateOfBirth())
                .setGender(request.getGender())
                .setRole(user.getRoles().toString())
                .setAddress(request.getAddress())
                .setCity(request.getCity())
                .setStateProvince(request.getStateProvince())
                .setPostalCode(request.getPostalCode())
                .setCountry(request.getCountry());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public ApiResponse<ModelDTO> removeUserRole(RoleUpdateRequest request, Long id) {
        User user = adminService.getUserById(id).orElseThrow(NotFoundException::notFound);
        if (user.getRoles().size() <= 1) throw ConflictException.userHasOneRole();
        user.getRoles().remove(request.getRole());
        adminService.updateUser(user);

        ModelDTO modelDTO = new ModelDTO()
                .setRole(user.getRoles().toString());
        return new ApiResponse<>(true, "Operation completed successfully", modelDTO);
    }

    public void deleteUser(Long id) {
        User user = adminService.getUserById(id).orElseThrow(NotFoundException::notFound);
        adminService.deleteUser(id);
    }

    public List<User> searchRoleUser(String role) {
        return adminService.searchRoleUser(role);
    }

}
