package com.example_login_2.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void init();

    String uploadProfilePicture(MultipartFile file);
}
