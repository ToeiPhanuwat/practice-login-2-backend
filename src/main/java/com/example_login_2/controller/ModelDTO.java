package com.example_login_2.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelDTO {

    private String email;
    //    private String password;
    private String firstName;
    private String lastName;
    private String role;
    private String phoneNumber;
    private Date dateOfBirth;
    private String gender;
    private String fileName;

    private String token;

    private String address;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;

    private String activationToken;
//    private Date expiresAt;
    private String activated;

    private String jwtToken;
//    private Date issuedAt;
//    private Date jwtExpiresAt;
//    private Boolean revoked;
}
