package com.example.user_management_rbac.dto;

import lombok.*;
import jakarta.validation.constraints.*;

public class AuthRequests {

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class Register {
        @Email @NotBlank
        private String email;

        @NotBlank @Size(min=8, message="Password must be at least 8 characters")
        private String password;

        private String firstName;
        private String lastName;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class Login {
        @Email @NotBlank
        private String email;

        @NotBlank
        private String password;
    }
}
