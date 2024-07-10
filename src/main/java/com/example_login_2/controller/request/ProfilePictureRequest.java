package com.example_login_2.controller.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ProfilePictureRequest {

    private MultipartFile profilePicture;
}
