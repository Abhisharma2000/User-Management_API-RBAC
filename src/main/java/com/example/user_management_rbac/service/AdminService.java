package com.example.user_management_rbac.service;

import com.example.user_management_rbac.model.Role;
import com.example.user_management_rbac.model.User;
import com.example.user_management_rbac.repository.RoleRepository;
import com.example.user_management_rbac.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AdminService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final AuditService auditService;

    public AdminService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder, AuditService auditService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.auditService=auditService;
    }

    public List<User> listUsers() {
        return userRepo.findAll();
    }

    public User getUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(String email, String password, String firstName, String lastName, List<String> roles) {
        if (userRepo.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        Set<Role> roleSet = roles.stream()
                .map(rn -> roleRepo.findByName(rn).orElseThrow(() -> new RuntimeException("Role not found: " + rn)))
                .collect(java.util.stream.Collectors.toSet());

        User u = User.builder()
                .email(email)
                .password(encoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .enabled(true)
                .locked(false)
                .roles(roleSet)
                .build();

        return userRepo.save(u);
    }

    public User updateUser(Long id, String firstName, String lastName, Boolean enabled, Boolean locked) {
        User u = getUser(id);
        boolean setStatus=false;
        if (firstName != null) u.setFirstName(firstName);
        if (lastName != null) u.setLastName(lastName);
        if (enabled != null && enabled!=u.isEnabled()) {u.setEnabled(enabled);setStatus=true;}
        if (locked != null && locked!=u.isLocked()) {u.setLocked(locked);setStatus=true;}
        User updated=userRepo.save(u);
        if (setStatus){
            auditService.record("USER_STATUS_UPDATED", "ADMIN", updated.getEmail(),
            "Enabled: " + updated.isEnabled() + ", Locked: " + updated.isLocked());
        }
        return updated;
    }

    public void deleteUser(Long id) {
        User u = getUser(id);
        userRepo.deleteById(id);
        auditService.record("DELETE_USER", "ADMIN", u.getEmail(),"User deleted by admin");

    }

    public void resetPassword(Long id, String newPassword) {
        User u = getUser(id);
        u.setPassword(encoder.encode(newPassword));
        auditService.record("RESET_PASSWORD", "ADMIN", u.getEmail(),"Password reset by admin");
        userRepo.save(u);
    }
}
