package com.example_login_2.service;

import com.example_login_2.exception.BadRequestException;
import com.example_login_2.exception.StorageException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class StorageServiceImp implements StorageService {

    @Value("${app.upload.path:images}")
    private String path;

    private Path rootLocation;

    @PostConstruct
    @Override
    public void init() {
        this.rootLocation = Paths.get(path);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException ex) {
            throw new StorageException("Could not init storage: " + ex.getMessage());
        }
    }

    @Override
    public String uploadProfilePicture(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        final long limitFile = 1048576 * 5;
        if (file.getSize() > limitFile) throw BadRequestException.fileMaxSize();

        String contentType = file.getContentType();
        if (contentType == null) throw BadRequestException.fileContentTypeIsNull();

        List<String> supportedTypes = Arrays.asList("image/jpeg", "image/png");
        if (!supportedTypes.contains(contentType))
            throw BadRequestException.unsupported();

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename(), "File name must not be null"));
        try {
            if (fileName.contains("..")) throw BadRequestException.currentDirectory();
            fileName = UUID.randomUUID() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                return fileName;
            }
        } catch (IOException ex) {
            throw new StorageException("Failed to store file: " + fileName + ", " + ex.getMessage());
        }
    }

}
