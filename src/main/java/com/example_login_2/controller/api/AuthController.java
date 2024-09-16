package com.example_login_2.controller.api;

import com.example_login_2.business.AuthBusiness;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthRequest.*;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.UpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping
    public ResponseEntity<ApiResponse<ModelDTO>> getUserInfo() {
        return ResponseEntity.ok(authBusiness.getUserById());
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ModelDTO>> updateUser(
            @RequestPart MultipartFile file,
            @RequestPart UpdateRequest request) {
        return ResponseEntity.ok(authBusiness.updateUser(file, request));
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
