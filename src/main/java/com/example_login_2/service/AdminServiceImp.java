package com.example_login_2.service;

import com.example_login_2.controller.request.RoleUpdateRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.User;
import com.example_login_2.repository.AdminRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
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

    @Transactional
    @Override
    public User updateUser(User user) {
        return adminRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUserRequest(User user, UpdateRequest request) {
        user
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setPhoneNumber(request.getPhoneNumber())
                .setDateOfBirth(request.getDateOfBirth())
                .setGender(request.getGender())
                .getRoles().add(request.getRole());
        return adminRepository.save(user);
    }

    @Transactional
    @Override
    public User removeRoleAndUpdate(User user, RoleUpdateRequest role) {
        user.getRoles().remove(role.getRole());
        return adminRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        adminRepository.deleteById(id);
    }

    @Override
    public List<User> searchRoleUser(String role) {
        log.info("Find users with role: {} Successful.", role);
        return adminRepository.findByRoles(role);
    }
}
