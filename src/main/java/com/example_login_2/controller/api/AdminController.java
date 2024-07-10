package com.example_login_2.controller.api;

import com.example_login_2.business.AdminBusiness;
import com.example_login_2.controller.ApiResponse;
import com.example_login_2.controller.ModelDTO;
import com.example_login_2.controller.request.RoleUpdateRequest;
import com.example_login_2.controller.request.UpdateRequest;
import com.example_login_2.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminBusiness adminBusiness;

    public AdminController(AdminBusiness adminBusiness) {
        this.adminBusiness = adminBusiness;
    }

    @GetMapping //TODO: BUC
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(adminBusiness.getAllUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModelDTO>> getUser(@PathVariable long id) {
        return ResponseEntity.ok(adminBusiness.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModelDTO>> editUser(
            @RequestParam UpdateRequest request, @PathVariable long id) {
        return ResponseEntity.ok(adminBusiness.updateUser(request, id));
    }

    @PutMapping("removeRole/{id}")
    public ResponseEntity<ApiResponse<ModelDTO>> removeUserRole(
            @RequestBody RoleUpdateRequest request, @PathVariable long id) {
        return ResponseEntity.ok(adminBusiness.removeUserRole(request, id));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) { //TODO: BUC ลองเปลี่ยนเป็น Long id
        adminBusiness.deleteUser(id);
    }

    @GetMapping(path = "/search", params = "role")
    public ResponseEntity<List<User>> searchByRole(@RequestParam String role) {
        return ResponseEntity.ok(adminBusiness.searchRoleUser(role));
    }
}
