package com.example.user_management_rbac.service;

import com.example.user_management_rbac.model.Role;
import com.example.user_management_rbac.model.User;
import com.example.user_management_rbac.repository.RoleRepository;
import com.example.user_management_rbac.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SuperAdminService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final AuditService auditService;

    public SuperAdminService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder,AuditService auditService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.auditService=auditService;
    }

    /** Create a new Admin */
    public User createAdmin(String email, String password, String firstName, String lastName) {
        if (userRepo.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        User admin = User.builder()
                .email(email)
                .password(encoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .enabled(true)
                .locked(false)
                .roles(Set.of(adminRole))
                .build();

        return userRepo.save(admin);
    }

    /** Assign role to user */
    public User assignRole(Long userId, String roleName) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        auditService.record("ROLE_ASSIGNED", "SUPER_ADMIN", user.getEmail(), "Assigned role: " +
                roleName);

        return userRepo.save(user);
    }

    /** Remove role from user */
    public User removeRole(Long userId, String roleName) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Prevent removing last SUPER_ADMIN
        if (roleName.equals("ROLE_SUPER_ADMIN") &&
                user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_SUPER_ADMIN"))) {
            long count = userRepo.findAll().stream()
                    .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_SUPER_ADMIN")))
                    .count();
            if (count <= 1) {
                throw new RuntimeException("Cannot remove the last SUPER_ADMIN");
            }
        }user.getRoles().remove(role);
        auditService.record("ROLE_REMOVED", "SUPER_ADMIN", user.getEmail(),
                "Removed role: " + roleName);
        return userRepo.save(user);
    }
}
