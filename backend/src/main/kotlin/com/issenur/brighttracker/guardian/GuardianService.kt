package com.issenur.brighttracker.guardian

import com.issenur.brighttracker.audit.AuditAction
import com.issenur.brighttracker.audit.AuditResourceType
import com.issenur.brighttracker.audit.AuditService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GuardianService(
    private val guardianRepository: GuardianRepository,
    private val auditService: AuditService,
) {

    @Transactional
    fun create(request: GuardianRequest): GuardianResponse {
        val guardian = Guardian(
            firstName = request.firstName,
            lastName = request.lastName,
            phoneNumber = request.phoneNumber,
            email = request.email
        )

        val savedGuardian = guardianRepository.save(guardian)

        auditService.record(
            action = AuditAction.CREATE,
            resourceType = AuditResourceType.GUARDIAN,
            resourceId = savedGuardian.id,
        )

        return savedGuardian.toResponse()
    }

    @Transactional(readOnly = true)
    fun findAll(): List<GuardianResponse> =
        guardianRepository
            .findAll()
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findById(id: Long): GuardianResponse =
        findGuardian(id).toResponse()

    @Transactional
    fun update(
        id: Long,
        request: GuardianRequest
    ): GuardianResponse {
        val guardian = findGuardian(id)

        guardian.firstName = request.firstName
        guardian.lastName = request.lastName
        guardian.phoneNumber = request.phoneNumber
        guardian.email = request.email

        val savedGuardian = guardianRepository.save(guardian)

        auditService.record(
            action = AuditAction.UPDATE,
            resourceType = AuditResourceType.GUARDIAN,
            resourceId = savedGuardian.id,
        )

        return savedGuardian.toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        guardianRepository.delete(findGuardian(id))

        auditService.record(
            action = AuditAction.DELETE,
            resourceType = AuditResourceType.GUARDIAN,
            resourceId = id,
        )
    }

    private fun findGuardian(id: Long): Guardian =
        guardianRepository.findById(id)
            .orElseThrow { GuardianNotFoundException(id) }

    private fun Guardian.toResponse() =
        GuardianResponse(
            id = requireNotNull(id),
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}