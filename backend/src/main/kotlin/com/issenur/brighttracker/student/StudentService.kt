package com.issenur.brighttracker.student

import com.issenur.brighttracker.audit.AuditAction
import com.issenur.brighttracker.audit.AuditResourceType
import com.issenur.brighttracker.audit.AuditService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val auditService: AuditService,
) {

    @Transactional
    fun create(request: StudentRequest): StudentResponse {
        val student = Student(
            firstName = request.firstName,
            lastName = request.lastName,
            dateOfBirth = request.dateOfBirth,
            gradeLevel = request.gradeLevel,
            status = request.status
        )

        val savedStudent = studentRepository.save(student)

        auditService.record(
            action = AuditAction.CREATE,
            resourceType = AuditResourceType.STUDENT,
            resourceId = savedStudent.id,
        )

        return savedStudent.toResponse()
    }

    @Transactional(readOnly = true)
    fun findAll(): List<StudentResponse> =
        studentRepository.findAll().map { it.toResponse() }

    private fun Student.toResponse() =
        StudentResponse(
            id = requireNotNull(id),
            firstName = firstName,
            lastName = lastName,
            dateOfBirth = dateOfBirth,
            gradeLevel = gradeLevel,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    @Transactional(readOnly = true)
    fun findById(id: Long): StudentResponse =
        findStudent(id).toResponse()

    @Transactional
    fun update(id: Long, request: StudentRequest): StudentResponse {
        val student = findStudent(id)

        student.firstName = request.firstName
        student.lastName = request.lastName
        student.dateOfBirth = request.dateOfBirth
        student.gradeLevel = request.gradeLevel
        student.status = request.status

        val savedStudent = studentRepository.save(student)

        auditService.record(
            action = AuditAction.UPDATE,
            resourceType = AuditResourceType.STUDENT,
            resourceId = savedStudent.id,
        )

        return savedStudent.toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        val student = findStudent(id)

        studentRepository.delete(student)

        auditService.record(
            action = AuditAction.DELETE,
            resourceType = AuditResourceType.STUDENT,
            resourceId = id,
        )
    }

    private fun findStudent(id: Long): Student =
        studentRepository.findById(id)
            .orElseThrow { StudentNotFoundException(id) }
}