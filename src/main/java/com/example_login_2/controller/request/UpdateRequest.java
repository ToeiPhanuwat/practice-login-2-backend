package com.example_login_2.controller.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter
public class UpdateRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Date dateOfBirth;
    private String gender;
//    private MultipartFile file;

    @Setter(AccessLevel.PUBLIC)
    private String fileName;

    private String roles;

    private String address;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;
}
