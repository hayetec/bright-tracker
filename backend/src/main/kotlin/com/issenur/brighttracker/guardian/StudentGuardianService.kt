package com.issenur.brighttracker.guardian

import com.issenur.brighttracker.audit.AuditAction
import com.issenur.brighttracker.audit.AuditResourceType
import com.issenur.brighttracker.audit.AuditService
import com.issenur.brighttracker.student.StudentNotFoundException
import com.issenur.brighttracker.student.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudentGuardianService(
    private val studentGuardianRepository: StudentGuardianRepository,
    private val studentRepository: StudentRepository,
    private val guardianRepository: GuardianRepository,
    private val auditService: AuditService,
) {

    @Transactional
    fun link(
        studentId: Long,
        guardianId: Long,
        request: StudentGuardianRequest
    ): StudentGuardianResponse {

        if (!studentRepository.existsById(studentId)) {
            throw StudentNotFoundException(studentId)
        }

        if (!guardianRepository.existsById(guardianId)) {
            throw GuardianNotFoundException(guardianId)
        }

        if (
            studentGuardianRepository.existsByStudentIdAndGuardianId(
                studentId,
                guardianId
            )
        ) {
            throw StudentGuardianAlreadyExistsException(
                studentId,
                guardianId
            )
        }

        val relationship = StudentGuardian(
            studentId = studentId,
            guardianId = guardianId,
            relationship = request.relationship,
            isPrimaryContact = request.isPrimaryContact,
            isEmergencyContact = request.isEmergencyContact
        )

        val savedRelationship =
            studentGuardianRepository.save(relationship)

        auditService.record(
            action = AuditAction.ASSIGN,
            resourceType = AuditResourceType.STUDENT_GUARDIAN,
            resourceId = savedRelationship.id,
        )

        return savedRelationship.toResponse()
    }

    @Transactional(readOnly = true)
    fun findGuardiansByStudent(
        studentId: Long
    ): List<StudentGuardianResponse> {

        if (!studentRepository.existsById(studentId)) {
            throw StudentNotFoundException(studentId)
        }

        return studentGuardianRepository
            .findAllByStudentId(studentId)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun findStudentsByGuardian(
        guardianId: Long
    ): List<StudentGuardianResponse> {

        if (!guardianRepository.existsById(guardianId)) {
            throw GuardianNotFoundException(guardianId)
        }

        return studentGuardianRepository
            .findAllByGuardianId(guardianId)
            .map { it.toResponse() }
    }

    @Transactional
    fun update(
        studentId: Long,
        guardianId: Long,
        request: StudentGuardianRequest
    ): StudentGuardianResponse {

        val relationship =
            findRelationship(studentId, guardianId)

        relationship.relationship = request.relationship
        relationship.isPrimaryContact = request.isPrimaryContact
        relationship.isEmergencyContact = request.isEmergencyContact

        val savedRelationship =
            studentGuardianRepository.save(relationship)

        auditService.record(
            action = AuditAction.UPDATE,
            resourceType = AuditResourceType.STUDENT_GUARDIAN,
            resourceId = savedRelationship.id,
        )

        return savedRelationship.toResponse()
    }

    @Transactional
    fun remove(
        studentId: Long,
        guardianId: Long
    ) {
        val relationship =
            findRelationship(studentId, guardianId)

        val relationshipId = requireNotNull(relationship.id)

        studentGuardianRepository.delete(relationship)

        auditService.record(
            action = AuditAction.REMOVE,
            resourceType = AuditResourceType.STUDENT_GUARDIAN,
            resourceId = relationshipId,
        )
    }

    private fun findRelationship(
        studentId: Long,
        guardianId: Long
    ): StudentGuardian =
        studentGuardianRepository
            .findByStudentIdAndGuardianId(
                studentId,
                guardianId
            )
            ?: throw StudentGuardianNotFoundException(
                studentId,
                guardianId
            )

    private fun StudentGuardian.toResponse() =
        StudentGuardianResponse(
            id = requireNotNull(id),
            studentId = studentId,
            guardianId = guardianId,
            relationship = relationship,
            isPrimaryContact = isPrimaryContact,
            isEmergencyContact = isEmergencyContact,
            createdAt = createdAt
        )
}