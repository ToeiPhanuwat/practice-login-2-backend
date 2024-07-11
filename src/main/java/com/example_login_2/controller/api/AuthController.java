package com.example_login_2.controller.api;

import com.example_login_2.business.AuthBusiness;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthRequest.*;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.*;
import com.example_login_2.exception.BadRequestException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
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
    public ApiResponse<ModelDTO> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(fieldError -> {
                throw BadRequestException.validateException(
                        fieldError.getField() + " : " + fieldError.getDefaultMessage());
            });
        }

        return authBusiness.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ModelDTO>> login(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(fieldError -> {
                throw BadRequestException.validateException(fieldError.getField() + " : " + fieldError.getDefaultMessage());
            });
        }
        return ResponseEntity.ok(authBusiness.login(request));
    }

    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<ModelDTO>> activate(@RequestBody ActivateRequest request) {
        ApiResponse<ModelDTO> response = authBusiness.activate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-activation-email")
    public ResponseEntity<ApiResponse<String>> resendActivationEmail(@RequestBody ResendActivationEmailRequest request) {
        return ResponseEntity.ok(authBusiness.resendActivationEmail(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ModelDTO>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authBusiness.forgotPassword(request));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(authBusiness.resetPassword(request));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<ModelDTO>> getUser() {
        return ResponseEntity.ok(authBusiness.getUserById());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping
    public ResponseEntity<ApiResponse<ModelDTO>> putUser(
            @RequestPart MultipartFile file,
            @RequestPart UpdateRequest request) {
        return ResponseEntity.ok(authBusiness.updateUser(file, request));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteUser() {
        authBusiness.deleteUser();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/refresh-token")
    public ResponseEntity<ApiResponse<ModelDTO>> refreshToken() {
        return ResponseEntity.ok(authBusiness.refreshJwtToken());
    }
}
