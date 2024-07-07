package com.example_login_2.service;

import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.Address;
import com.example_login_2.model.User;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User updateUser(User user);

    User updateUserRequest(User user, UpdateRequest request);

    User updateAddress(User user, Address address);

    void deleteUser(Long id);


}
