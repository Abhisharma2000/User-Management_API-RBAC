package com.example.user_management_rbac.repository;

import com.example.user_management_rbac.model.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditEvent, Long> {
}
