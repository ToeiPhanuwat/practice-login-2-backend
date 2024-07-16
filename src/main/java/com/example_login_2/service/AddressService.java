package com.example_login_2.service;

import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.Address;
import com.example_login_2.model.User;
import org.springframework.cache.annotation.CachePut;

import java.util.Optional;

public interface AddressService {

    Address createAddress(User user);

    @CachePut(value = "user", key = "#user.id")
    Address updateAddress(User user, UpdateRequest request);

    Optional<Address> getAddressByUserId(Long userId);
}
