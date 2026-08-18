package com.issenur.brighttracker.assignment

import com.issenur.brighttracker.audit.AuditAction
import com.issenur.brighttracker.audit.AuditResourceType
import com.issenur.brighttracker.audit.AuditService
import com.issenur.brighttracker.classroom.ClassroomNotFoundException
import com.issenur.brighttracker.classroom.ClassroomRepository
import com.issenur.brighttracker.staff.StaffNotFoundException
import com.issenur.brighttracker.staff.StaffRepository
import com.issenur.brighttracker.staff.StaffRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StaffAssignmentService(
    private val assignmentRepository: StaffAssignmentRepository,
    private val classroomRepository: ClassroomRepository,
    private val staffRepository: StaffRepository,
    private val auditService: AuditService,
) {

    @Transactional
    fun assign(
        classroomId: Long,
        staffId: Long
    ): StaffAssignmentResponse {

        if (!classroomRepository.existsById(classroomId)) {
            throw ClassroomNotFoundException(classroomId)
        }

        val staff = staffRepository.findById(staffId)
            .orElseThrow { StaffNotFoundException(staffId) }

        if (
            staff.role != StaffRole.TEACHER &&
            staff.role != StaffRole.TEACHER_AIDE
        ) {
            throw AssignmentInvalidRoleException(staffId)
        }

        if (
            assignmentRepository.existsByClassroomIdAndStaffId(
                classroomId,
                staffId
            )
        ) {
            throw AssignmentAlreadyExistsException(
                staffId,
                classroomId
            )
        }

        val assignment = StaffAssignment(
            classroomId = classroomId,
            staffId = staffId
        )

        val savedAssignment =
            assignmentRepository.save(assignment)

        auditService.record(
            action = AuditAction.ASSIGN,
            resourceType = AuditResourceType.STAFF_ASSIGNMENT,
            resourceId = savedAssignment.id,
        )

        return savedAssignment.toResponse()
    }

    @Transactional(readOnly = true)
    fun findByClassroom(
        classroomId: Long
    ): List<StaffAssignmentResponse> {

        if (!classroomRepository.existsById(classroomId)) {
            throw ClassroomNotFoundException(classroomId)
        }

        return assignmentRepository
            .findAllByClassroomId(classroomId)
            .map { it.toResponse() }
    }

    @Transactional
    fun remove(
        classroomId: Long,
        staffId: Long
    ) {
        val assignment =
            assignmentRepository.findByClassroomIdAndStaffId(
                classroomId,
                staffId
            )
                ?: throw AssignmentNotFoundException(
                    staffId,
                    classroomId
                )

        val assignmentId = requireNotNull(assignment.id)

        assignmentRepository.delete(assignment)

        auditService.record(
            action = AuditAction.REMOVE,
            resourceType = AuditResourceType.STAFF_ASSIGNMENT,
            resourceId = assignmentId,
        )
    }

    private fun StaffAssignment.toResponse() =
        StaffAssignmentResponse(
            id = requireNotNull(id),
            classroomId = classroomId,
            staffId = staffId,
            assignedAt = assignedAt
        )
}