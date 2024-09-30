package com.example_login_2.controller.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
public class UpdateRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Date dateOfBirth;
    private String gender;
    private String address;

    @Setter
    private String fileName;

    private String role;

}
