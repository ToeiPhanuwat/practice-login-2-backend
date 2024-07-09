package com.example_login_2.controller.api;

import com.example_login_2.business.AuthBusiness;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.*;
import com.example_login_2.exception.BadRequestException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody AuthRegisterRequest request,
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
            @Valid @RequestBody AuthLoginRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(fieldError -> {
                throw BadRequestException.validateException(fieldError.getField() + " : " + fieldError.getDefaultMessage());
            });
        }
        return ResponseEntity.ok(authBusiness.login(request));
    }

    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<ModelDTO>> activate(@RequestBody AuthActivateRequest request) {
        ApiResponse<ModelDTO> response = authBusiness.activate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-activation-email")
    public ResponseEntity<ApiResponse<String>> resendActivationEmail(@RequestBody AuthResendActivationEmailRequest request) {
        return ResponseEntity.ok(authBusiness.resendActivationEmail(request));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<ModelDTO>> getUser() {
        return ResponseEntity.ok(authBusiness.getUserById());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping
    public ResponseEntity<ApiResponse<ModelDTO>> editUser(@RequestBody UpdateRequest request) {
        return ResponseEntity.ok(authBusiness.updateUser(request));
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
