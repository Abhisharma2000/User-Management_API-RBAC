package com.example.user_management_rbac.controller;

import com.example.user_management_rbac.model.User;
import com.example.user_management_rbac.service.UserService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get logged-in user profile",
            description = "Returns the profile of the authenticated user(by passing email)",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    /** Get logged-in user profile */
    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        User user = userService.getByEmail(principal.getName());
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "roles", user.getRoles().stream().map(r -> r.getName()).toList()
        ));
    }

    /** Update profile */

    @Operation(summary = "Update logged-in user profile (firstName, lastName)",
    description = "Returns the updated profile of the user(by passing email,firstName,lastName)",
    security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PatchMapping("/me")
    public ResponseEntity<?> updateProfile(Principal principal,
                                           @RequestBody Map<String, String> body) {
        User user = userService.getByEmail(principal.getName());
        String firstName = body.getOrDefault("firstName", user.getFirstName());
        String lastName = body.getOrDefault("lastName", user.getLastName());
        User updated = userService.updateProfile(user, firstName, lastName);
        return ResponseEntity.ok(Map.of(
                "id", updated.getId(),
                "message", "Profile updated successfully"
        ));
    }

    /** Change password */
    @Operation(summary = "Change logged-in user password",description = "Password updated of the logged-in user(by passing email,oldPassword,newPassword)",
            security = { @SecurityRequirement(name = "bearerAuth")})
    @PatchMapping("/me/password")
    public ResponseEntity<?> changePassword(Principal principal,
                                            @RequestBody Map<String, String> body) {
        User user = userService.getByEmail(principal.getName());
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Old and new password required");
        }
        userService.changePassword(user, oldPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
