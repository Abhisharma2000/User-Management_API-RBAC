package com.example.user_management_rbac.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiError {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private int status;         // HTTP status code
    private String error;       // e.g. Unauthorized, Bad Request
    private String message;     // error message
    private String path;        // requested endpoint
    private String code;        // custom code like AUTH_FAILURE, VALIDATION_ERROR
}
