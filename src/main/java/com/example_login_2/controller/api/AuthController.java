package com.example_login_2.controller.api;

import com.example_login_2.business.AuthBusiness;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthRequest.*;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.User;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

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
        return ResponseEntity.ok(authBusiness.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ModelDTO>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authBusiness.login(request));
    }

    @GetMapping("/activate/{token}")
    public ResponseEntity<ApiResponse<ModelDTO>> activate(@PathVariable String token) {
        ApiResponse<ModelDTO> response = authBusiness.activate(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(authBusiness.logout());
    }

    @GetMapping("/resend-activation-email/{token}")
    public ResponseEntity<ApiResponse<String>> resendActivationEmail(@PathVariable String token) {
        return ResponseEntity.ok(authBusiness.resendActivationEmail(token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ModelDTO>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authBusiness.forgotPassword(request));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(authBusiness.resetPassword(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getUserProfile() {
        return ResponseEntity.ok(authBusiness.getUserById());
    }

    @PutMapping
    public ResponseEntity<ApiResponse<User>> updateUser(@RequestBody UpdateRequest request) {
        return ResponseEntity.ok(authBusiness.updateUser(request));
    }

    @PutMapping("/file")
    public ResponseEntity<ApiResponse<String>> updateUser(@RequestPart MultipartFile file) {
        return ResponseEntity.ok(authBusiness.updateUser(file));
    }

    @GetMapping("/images/{file}")
    public ResponseEntity<Resource> getImage(@PathVariable String file) throws MalformedURLException {
        Resource resource = authBusiness.getImage(file);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteUser() {
        authBusiness.deleteUser();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<ModelDTO>> refreshToken() {
        return ResponseEntity.ok(authBusiness.refreshJwtToken());
    }
}
