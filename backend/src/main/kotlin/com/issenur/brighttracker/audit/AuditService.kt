package com.issenur.brighttracker.audit

import com.issenur.brighttracker.security.CurrentUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuditService(
    private val auditLogRepository: AuditLogRepository,
    private val currentUser: CurrentUser,
) {

    @Transactional
    fun record(
        action: AuditAction,
        resourceType: AuditResourceType,
        resourceId: Long?,
        details: String? = null,
    ) {
        val auditLog = AuditLog(
            actorSubject = currentUser.subject(),
            actorUsername = currentUser.username(),
            action = action,
            resourceType = resourceType,
            resourceId = resourceId,
            details = details,
        )

        auditLogRepository.save(auditLog)
    }
}