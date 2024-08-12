package com.example_login_2.service;

import com.example_login_2.controller.request.RoleUpdateRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.Address;
import com.example_login_2.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    List<User> getAllUsers();

    @Cacheable(value = "user", key = "#id", unless = "#result == null")
    Optional<User> getUserById(Long id);

    @CachePut(value = "user", key = "#user.id")
    User updateUser(User user);

    @CachePut(value = "user", key = "#user.id")
    User updateUserRequest(User user, UpdateRequest request);

    @CachePut(value = "user", key = "#user.id")
    User removeRoleAndUpdate(User user, RoleUpdateRequest role);

    @CachePut(value = "user", key = "#user.id")
    User updateAddress(User user, Address address);

    @CacheEvict(value = "user", key = "#id")
    void deleteUser(Long id);

    List<User> searchRoleUser(String role);

}
