package com.issenur.brighttracker.staff

import com.issenur.brighttracker.audit.AuditAction
import com.issenur.brighttracker.audit.AuditResourceType
import com.issenur.brighttracker.audit.AuditService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StaffService(
    private val staffRepository: StaffRepository,
    private val auditService: AuditService,
) {

    @Transactional
    fun create(request: StaffRequest): StaffResponse {
        val staff = Staff(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            phoneNumber = request.phoneNumber,
            role = request.role,
            status = request.status
        )

        val savedStaff = staffRepository.save(staff)

        auditService.record(
            action = AuditAction.CREATE,
            resourceType = AuditResourceType.STAFF,
            resourceId = savedStaff.id,
        )

        return savedStaff.toResponse()
    }

    @Transactional(readOnly = true)
    fun findAll(): List<StaffResponse> =
        staffRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findById(id: Long): StaffResponse =
        findStaff(id).toResponse()

    @Transactional
    fun update(
        id: Long,
        request: StaffRequest,
    ): StaffResponse {
        val staff = findStaff(id)

        staff.firstName = request.firstName
        staff.lastName = request.lastName
        staff.email = request.email
        staff.phoneNumber = request.phoneNumber
        staff.role = request.role
        staff.status = request.status

        val savedStaff = staffRepository.save(staff)

        auditService.record(
            action = AuditAction.UPDATE,
            resourceType = AuditResourceType.STAFF,
            resourceId = savedStaff.id,
        )

        return savedStaff.toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        staffRepository.delete(findStaff(id))

        auditService.record(
            action = AuditAction.DELETE,
            resourceType = AuditResourceType.STAFF,
            resourceId = id,
        )
    }

    private fun findStaff(id: Long): Staff =
        staffRepository.findById(id)
            .orElseThrow { StaffNotFoundException(id) }

    private fun Staff.toResponse() =
        StaffResponse(
            id = requireNotNull(id),
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber,
            role = role,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}