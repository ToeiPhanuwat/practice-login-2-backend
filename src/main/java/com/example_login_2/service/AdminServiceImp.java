package com.example_login_2.service;

import com.example_login_2.controller.request.RoleUpdateRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.Address;
import com.example_login_2.model.User;
import com.example_login_2.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImp implements AdminService {

    private final AdminRepository adminRepository;

    public AdminServiceImp(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return adminRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return adminRepository.findById(id);
    }

    @Override
    public User updateUser(User user) {
        return adminRepository.save(user);
    }

    @Override
    public User updateUserRequest(User user, UpdateRequest request) {
        if (request.getFileName() != null) {
            user.setFileName(request.getFileName());
        }
        user
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setPhoneNumber(request.getPhoneNumber())
                .setDateOfBirth(request.getDateOfBirth())
                .setGender(request.getGender())
                .getRoles().add(request.getRole());
        return adminRepository.save(user);
    }

    @Override
    public User updateAddress(User user, Address address) {
        user = user.setAddress(address);
        return adminRepository.save(user);
    }

    @Override
    public User removeRoleAndUpdate(User user, RoleUpdateRequest role) {
        user.getRoles().remove(role.getRole());
        return adminRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        adminRepository.deleteById(id);
    }

    @Override
    public List<User> searchRoleUser(String role) {
        return adminRepository.findByRoles(role);
    }
}
