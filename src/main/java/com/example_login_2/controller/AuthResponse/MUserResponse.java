package com.example_login_2.controller.AuthResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class MUserResponse {

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private Date dateOfBirth;

    private String gender;

    private String fileName;

    private String address;
}
