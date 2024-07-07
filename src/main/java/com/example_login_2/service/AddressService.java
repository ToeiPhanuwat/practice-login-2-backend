package com.example_login_2.service;

import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.Address;
import com.example_login_2.model.User;

import java.util.Optional;

public interface AddressService {

    Address createAddress(User user, UpdateRequest request);

    Address updateAddressUser(User user, Address address, UpdateRequest request);

    Optional<Address> getAddressByUserId(Long userId);
}
