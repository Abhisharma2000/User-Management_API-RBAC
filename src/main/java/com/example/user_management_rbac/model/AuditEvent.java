package com.example.user_management_rbac.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "audit_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;  // e.g. LOGIN_SUCCESS, USER_CREATED
    private String actor;      // who performed the action
    private String target;     // on which user/entity the action happened

    @Lob
    private String details;    // extra details (JSON/string)

    private Instant createdAt;
}
