package com.example_login_2.service;

import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.Address;
import com.example_login_2.model.User;
import com.example_login_2.repository.AdminRepository;
import org.springframework.data.domain.Sort;
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
        return adminRepository.findAll(Sort.by(Sort.Direction.ASC, "email"));
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
        user
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setPhoneNumber(request.getPhoneNumber())
                .setDateOfBirth(request.getDateOfBirth())
                .setGender(request.getGender())
                .setProfilePicture(request.getProfilePicture())
                .getRoles().add(request.getRoles());
        return adminRepository.save(user);
    }

    @Override
    public User updateAddress(User user, Address address) {
        user = user.setAddress(address);
        return adminRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        adminRepository.deleteById(id);
    }


}