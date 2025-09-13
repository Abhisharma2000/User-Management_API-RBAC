package com.example.user_management_rbac.service;

import com.example.user_management_rbac.model.AuditEvent;
import com.example.user_management_rbac.repository.AuditRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditService {

    private final AuditRepository auditRepo;

    public AuditService(AuditRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    /** Save audit event */
    public void record(String eventType, String actor, String target, String details) {
        AuditEvent event = AuditEvent.builder()
                .eventType(eventType)
                .actor(actor)
                .target(target)
                .details(details)
                .createdAt(Instant.now())
                .build();
        auditRepo.save(event);
    }
}
