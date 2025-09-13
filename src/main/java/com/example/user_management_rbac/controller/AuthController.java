package com.example.user_management_rbac.controller;

import com.example.user_management_rbac.dto.AuthRequests;
import com.example.user_management_rbac.model.User;
import com.example.user_management_rbac.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @Operation(summary = "Register a new user", description = "Creates a new user with ROLE_USER by default")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @ApiResponse(responseCode = "409", description = "Email already exists")

    /** Register endpoint */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequests.Register req) {
        User user = authService.register(req);
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "message", "User registered successfully"
        ));
    }

    @Operation(summary = "User login", description = "Authenticates user and returns JWT access token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    /** Login endpoint */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequests.Login req) {
        String token = authService.login(req);
        return ResponseEntity.ok(Map.of("accessToken", token));
    }
}
