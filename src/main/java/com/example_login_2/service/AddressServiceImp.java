package com.example_login_2.service;

import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.Address;
import com.example_login_2.model.User;
import com.example_login_2.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressServiceImp implements AddressService {

    private final AddressRepository addressRepository;

    public AddressServiceImp(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public Address createAddress(User user) {
        Address address = new Address()
                .setUser(user);
        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(User user, UpdateRequest request) {
        Address address = user.getAddress()
                .setUser(user)
                .setAddress(request.getAddress())
                .setCity(request.getCity())
                .setStateProvince(request.getStateProvince())
                .setPostalCode(request.getPostalCode())
                .setCountry(request.getCountry());
        return addressRepository.save(address);
    }

    @Override
    public Optional<Address> getAddressByUserId(Long userId) {
        return addressRepository.findByUserId(userId);
    }
}
