package com.example_login_2.controller.api;

import com.example_login_2.business.AdminBusiness;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.AuthResponse.MUserResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.RoleUpdateRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.User;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminBusiness adminBusiness;

    public AdminController(AdminBusiness adminBusiness) {
        this.adminBusiness = adminBusiness;
    }

    @GetMapping
    public ResponseEntity<List<MUserResponse>> getUsers() {
        log.info("Received a request from the administrator to fetch all user data");
        return ResponseEntity.ok(adminBusiness.getAllUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MUserResponse>> getUser(@PathVariable Long id) {
        log.info("Received request from admin to fetch user ID: {}", id);
        return ResponseEntity.ok(adminBusiness.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MUserResponse>> putUser(
            @RequestBody UpdateRequest request,
            @PathVariable long id) {
        log.info("Received a request from the administrator to update the user ID: {} with the request: {}", id, request);
        return ResponseEntity.ok(adminBusiness.updateUser(request, id));
    }

    @PutMapping("removeRole/{id}")
    public ResponseEntity<ApiResponse<ModelDTO>> removeUserRole(
            @Valid @RequestBody RoleUpdateRequest request, @PathVariable long id) {
        log.info("Received a request from the administrator to delete a role from user ID : {} role: {}", id, request);
        return ResponseEntity.ok(adminBusiness.removeUserRole(request, id));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("Received a request from the administrator to delete a user ID: {}", id);
        adminBusiness.deleteUser(id);
    }

    @GetMapping(path = "/search", params = "role")
    public ResponseEntity<List<User>> searchByRole(@RequestParam String role) {
        log.info("Received a request from the administrator to search for users by role: {}", role);
        return ResponseEntity.ok(adminBusiness.searchRoleUser(role));
    }
}
