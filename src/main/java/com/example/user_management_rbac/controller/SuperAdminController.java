package com.example.user_management_rbac.controller;

import com.example.user_management_rbac.model.User;
import com.example.user_management_rbac.service.SuperAdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Map;
// Some special rights having superAdmin

@RestController
@RequestMapping("/api/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    @Operation(summary = "Create a new Admin user",description = "Admin user created(email,password,firstName,lastName)by super-admin",
            security = { @SecurityRequirement(name = "bearerAuth")})
    /** Create a new Admin */
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.getOrDefault("password", "Admin@123");
        String firstName = body.get("firstName");
        String lastName = body.get("lastName");

        User admin = superAdminService.createAdmin(email, password, firstName, lastName);
        return ResponseEntity.ok(Map.of(
                "id", admin.getId(),
                "email", admin.getEmail(),
                "message", "Admin created successfully"
        ));
    }

    @Operation(summary = "Assign role to user",description = "Role is assign to the user(by passing id,role) by super-admin",
            security = { @SecurityRequirement(name = "bearerAuth")})
    /** Assign role to user */
    @PatchMapping("/{userId}/assign-role")
    public ResponseEntity<?> assignRole(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String role = body.get("role");
        User u = superAdminService.assignRole(userId, role);
        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "roles", u.getRoles().stream().map(r -> r.getName()).toList()
        ));
    }

    @Operation(summary = "Remove role from user (cannot remove last SUPER_ADMIN)",description = "Remove the role from user(by passing id,role) by super-admin acess",
            security = { @SecurityRequirement(name = "bearerAuth")})
    /** Remove role from user */
    @PatchMapping("/{userId}/remove-role")
    public ResponseEntity<?> removeRole(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String role = body.get("role");
        User u = superAdminService.removeRole(userId, role);
        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "roles", u.getRoles().stream().map(r -> r.getName()).toList()
        ));
    }
}
