package com.issenur.brighttracker.classroom

import com.issenur.brighttracker.audit.AuditAction
import com.issenur.brighttracker.audit.AuditResourceType
import com.issenur.brighttracker.audit.AuditService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClassroomService(
    private val classroomRepository: ClassroomRepository,
    private val auditService: AuditService,
) {

// ClassroomService

    @Transactional
    fun create(request: ClassroomRequest): ClassroomResponse {
        val classroom = Classroom(
            name = request.name,
            gradeLevel = request.gradeLevel,
            roomNumber = request.roomNumber,
            capacity = request.capacity,
            status = request.status
        )

        val savedClassroom = classroomRepository.save(classroom)

        auditService.record(
            action = AuditAction.CREATE,
            resourceType = AuditResourceType.CLASSROOM,
            resourceId = savedClassroom.id,
        )

        return savedClassroom.toResponse()
    }

    @Transactional(readOnly = true)
    fun findAll(): List<ClassroomResponse> =
        classroomRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findById(id: Long): ClassroomResponse =
        findClassroom(id).toResponse()

    @Transactional
    fun update(
        id: Long,
        request: ClassroomRequest
    ): ClassroomResponse {
        val classroom = findClassroom(id)

        classroom.name = request.name
        classroom.gradeLevel = request.gradeLevel
        classroom.roomNumber = request.roomNumber
        classroom.capacity = request.capacity
        classroom.status = request.status

        val savedClassroom = classroomRepository.save(classroom)

        auditService.record(
            action = AuditAction.UPDATE,
            resourceType = AuditResourceType.CLASSROOM,
            resourceId = savedClassroom.id,
        )

        return savedClassroom.toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        classroomRepository.delete(findClassroom(id))

        auditService.record(
            action = AuditAction.DELETE,
            resourceType = AuditResourceType.CLASSROOM,
            resourceId = id,
        )
    }
    private fun findClassroom(id: Long): Classroom =
        classroomRepository.findById(id)
            .orElseThrow { ClassroomNotFoundException(id) }

    private fun Classroom.toResponse() =
        ClassroomResponse(
            id = requireNotNull(id),
            name = name,
            gradeLevel = gradeLevel,
            roomNumber = roomNumber,
            capacity = capacity,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}