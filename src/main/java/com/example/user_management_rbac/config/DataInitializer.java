package com.example.user_management_rbac.config;

import com.example.user_management_rbac.model.Role;
import com.example.user_management_rbac.model.User;
import com.example.user_management_rbac.repository.RoleRepository;
import com.example.user_management_rbac.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder encoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        Role superRole = roleRepo.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_SUPER_ADMIN")));
        roleRepo.findByName("ROLE_ADMIN").orElseGet(() -> roleRepo.save(new Role(null, "ROLE_ADMIN")));
        roleRepo.findByName("ROLE_USER").orElseGet(() -> roleRepo.save(new Role(null, "ROLE_USER")));

        String superEmail = "superAdmin@gmail.com";
        if (!userRepo.existsByEmail(superEmail)) {
            User su = User.builder()
                    .email(superEmail)
                    .password(encoder.encode("superAdmin@2025"))
                    .firstName("Super")
                    .lastName("Admin")
                    .enabled(true)
                    .locked(false)
                    .roles(Set.of(superRole))
                    .build();
            userRepo.save(su);
            System.out.println("Super admin created: " + superEmail);
        }
    }
}
