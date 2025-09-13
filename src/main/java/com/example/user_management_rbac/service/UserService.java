package com.example.user_management_rbac.service;

import com.example.user_management_rbac.model.User;
import com.example.user_management_rbac.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuditService auditService;

    public UserService(UserRepository userRepo, PasswordEncoder encoder,AuditService auditService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.auditService=auditService;
    }

    /** Fetch user by email (principal.getName()) */
    public User getByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /** Update profile (firstName, lastName only) */
    public User updateProfile(User user, String firstName, String lastName) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return userRepo.save(user);
    }

    /** Change password */
    public void changePassword(User user, String oldPassword, String newPassword) {
        if (!encoder.matches(oldPassword, user.getPassword())) {
            auditService.record("PASSWORD_CHANGE_FAILED", user.getEmail(),
                    user.getEmail(), "Old password mismatch");
            throw new RuntimeException("Old password is incorrect");
        }
        user.setPassword(encoder.encode(newPassword));
        auditService.record("PASSWORD_CHANGED", user.getEmail(),
                user.getEmail(), "Password updated");
        userRepo.save(user);
    }
}
