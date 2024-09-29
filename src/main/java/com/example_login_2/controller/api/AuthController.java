package com.example_login_2.controller.api;

import com.example_login_2.business.AuthBusiness;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthRequest.ForgotPasswordRequest;
import com.example_login_2.controller.AuthRequest.LoginRequest;
import com.example_login_2.controller.AuthRequest.PasswordResetRequest;
import com.example_login_2.controller.AuthRequest.RegisterRequest;
import com.example_login_2.controller.AuthResponse.MUserResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.UpdateRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

@Log4j2
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthBusiness authBusiness;

    public AuthController(AuthBusiness authBusiness) {
        this.authBusiness = authBusiness;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registers")
    public ResponseEntity<ApiResponse<ModelDTO>> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Received user registration request for email: {}", request.getEmail());
        return ResponseEntity.ok(authBusiness.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ModelDTO>> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("Received login request for email: {}", request.getEmail());
        return ResponseEntity.ok(authBusiness.login(request));
    }

    @GetMapping("/activate/{token}")
    public ResponseEntity<ApiResponse<ModelDTO>> activate(@PathVariable String token) {
        log.info("Received activation request with token: {}", token);
        ApiResponse<ModelDTO> response = authBusiness.activate(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        log.info("User requested to logout");
        return ResponseEntity.ok(authBusiness.logout());
    }

    @GetMapping("/resend-activation-email/{token}")
    public ResponseEntity<ApiResponse<String>> resendActivationEmail(@PathVariable String token) {
        log.info("Resend activation email request received for token: {}", token);
        return ResponseEntity.ok(authBusiness.resendActivationEmail(token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ModelDTO>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Received forgot password request for email: {}", request.getEmail());
        return ResponseEntity.ok(authBusiness.forgotPassword(request));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request) {
        log.info("Received reset password request for token: {}", request.getToken());
        return ResponseEntity.ok(authBusiness.resetPassword(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<MUserResponse>> getUserProfile() {
        log.info("Fetching user profile");
        return ResponseEntity.ok(authBusiness.getUserById());
    }

    @PutMapping
    public ResponseEntity<ApiResponse<MUserResponse>> updateUser(@RequestBody UpdateRequest request) {
        log.info("Updating user with request: {}", request);
        return ResponseEntity.ok(authBusiness.updateUser(request));
    }

    @PutMapping("/file")
    public ResponseEntity<ApiResponse<String>> updateUser(@RequestPart MultipartFile file) {
        log.info("Updating user file");
        return ResponseEntity.ok(authBusiness.updateUser(file));
    }

    @GetMapping("/images/{file}")
    public ResponseEntity<Resource> getImage(@PathVariable String file) throws MalformedURLException {
        log.info("Fetching image: {}", file);
        Resource resource = authBusiness.getImage(file);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteUser() {
        log.info("Deleting user");
        authBusiness.deleteUser();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<ModelDTO>> refreshToken() {
        log.info("Refreshing JWT token");
        return ResponseEntity.ok(authBusiness.refreshJwtToken());
    }
}
