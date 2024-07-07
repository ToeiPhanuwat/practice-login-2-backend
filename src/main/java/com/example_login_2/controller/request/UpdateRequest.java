package com.example_login_2.controller.request;

import lombok.Getter;

import java.util.Date;

@Getter
public class UpdateRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Date dateOfBirth;
    private String gender;
    private String profilePicture;

    private String roles;

    private String address;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;
}
