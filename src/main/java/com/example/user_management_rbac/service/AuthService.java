package com.example.user_management_rbac.service;

import com.example.user_management_rbac.dto.AuthRequests;
import com.example.user_management_rbac.model.Role;
import com.example.user_management_rbac.model.User;
import com.example.user_management_rbac.repository.RoleRepository;
import com.example.user_management_rbac.repository.UserRepository;
import com.example.user_management_rbac.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.user_management_rbac.service.AuditService;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final AuditService auditService;

    public AuthService(UserRepository userRepo, RoleRepository roleRepo,
                       PasswordEncoder encoder, JwtUtil jwtUtil,AuditService auditService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.auditService=auditService;
    }

    /** Register a new user with default ROLE_USER */
    public User register(AuthRequests.Register req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role roleUser = roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User u = User.builder()
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword())) // password hashing
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .enabled(true)
                .locked(false)
                .roles(Set.of(roleUser))
                .build();

        return userRepo.save(u);
    }

    /** Authenticate and return JWT token */
    public String login(AuthRequests.Login req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() ->{
                    auditService.record("LOGIN_FAILURE", req.getEmail(), null,
                            "User not found");
                    return new RuntimeException("User not found");
                });

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            auditService.record("LOGIN_FAILURE", req.getEmail(), user.getEmail(),
                    "Invalid password");
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isEnabled() || user.isLocked()) {
            auditService.record("LOGIN_FAILURE", req.getEmail(), user.getEmail(),
                    "Account disabled or locked");
            throw new RuntimeException("Account disabled or locked");
        }
        auditService.record("LOGIN_SUCCESS", user.getEmail(), user.getEmail(),
                "User logged in");
        var roles = user.getRoles().stream().map(Role::getName).toList();
        return jwtUtil.generateToken(user.getEmail(), roles);
    }
}
