package com.example.user_management_rbac.controller;

import com.example.user_management_rbac.model.User;
import com.example.user_management_rbac.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "List all users",
    description = "Returns all user list by admin acess",
    security = { @SecurityRequirement(name = "bearerAuth")}

    )
    /** List all users */
    @GetMapping
    public ResponseEntity<?> listUsers() {
        List<User> users = adminService.listUsers();
        return ResponseEntity.ok(users.stream().map(u -> Map.of(
                "id", u.getId(),
                "email", u.getEmail(),
                "firstName", u.getFirstName(),
                "lastName", u.getLastName(),
                "enabled", u.isEnabled(),
                "locked", u.isLocked(),
                "roles", u.getRoles().stream().map(r -> r.getName()).toList()
        )).toList());
    }

    @Operation(summary = "Get a user by ID",description = "Returns the user by id at the time of admin acess,please pass the user id as response ",
            security = { @SecurityRequirement(name = "bearerAuth")})
    /** Get user by ID */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        User u = adminService.getUser(id);
        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "email", u.getEmail(),
                "firstName", u.getFirstName(),
                "lastName", u.getLastName(),
                "enabled", u.isEnabled(),
                "locked", u.isLocked(),
                "roles", u.getRoles().stream().map(r -> r.getName()).toList()
        ));
    }

    @Operation(summary = "Create a new user",description = "Create the new user(by passing email,password,firstName,lastName,roles) by admin",
            security = { @SecurityRequirement(name = "bearerAuth")})
    /** Create user */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        String password = (String) body.getOrDefault("password", "TempP@ssw0rd");
        String firstName = (String) body.get("firstName");
        String lastName = (String) body.get("lastName");
        List<String> roles = (List<String>) body.getOrDefault("roles", List.of("ROLE_USER"));

        User u = adminService.createUser(email, password, firstName, lastName, roles);
        return ResponseEntity.ok(Map.of("id", u.getId(), "email", u.getEmail()));
    }

    @Operation(summary = "Update user details (enable/disable, lock/unlock)",description = "Return the user with updated details(id,firstName,lastName,enabled,locked) like enable/disable,lock/unlock by admin",
            security = { @SecurityRequirement(name = "bearerAuth")})

    /** Update user */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        User u = adminService.updateUser(
                id,
                (String) body.get("firstName"),
                (String) body.get("lastName"),
                (Boolean) body.get("enabled"),
                (Boolean) body.get("locked")
        );
        return ResponseEntity.ok(Map.of("id", u.getId(), "message", "User updated"));
    }
    @Operation(summary = "Delete a user",description = "Delete the user(by passing id) by Admin",
            security = { @SecurityRequirement(name = "bearerAuth")})
    /** Delete user */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    @Operation(summary = "Reset a user's password",description = "Password reset(by passing id,newPassword) by Admin acess",
            security = { @SecurityRequirement(name = "bearerAuth")})
    /** Reset password */
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        adminService.resetPassword(id, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }
}
