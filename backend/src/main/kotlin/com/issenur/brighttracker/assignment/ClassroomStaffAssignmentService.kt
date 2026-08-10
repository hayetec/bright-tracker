package com.issenur.brighttracker.assignment

import com.issenur.brighttracker.classroom.ClassroomNotFoundException
import com.issenur.brighttracker.classroom.ClassroomRepository
import com.issenur.brighttracker.staff.StaffNotFoundException
import com.issenur.brighttracker.staff.StaffRepository
import com.issenur.brighttracker.staff.StaffRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClassroomStaffAssignmentService(
    private val assignmentRepository: ClassroomStaffAssignmentRepository,
    private val classroomRepository: ClassroomRepository,
    private val staffRepository: StaffRepository
) {

    @Transactional
    fun assign(
        classroomId: Long,
        staffId: Long
    ): ClassroomStaffAssignmentResponse {

        if (!classroomRepository.existsById(classroomId)) {
            throw ClassroomNotFoundException(classroomId)
        }

        val staff = staffRepository.findById(staffId)
            .orElseThrow { StaffNotFoundException(staffId) }

        if (
            staff.role != StaffRole.TEACHER &&
            staff.role != StaffRole.TEACHER_AIDE
        ) {
            throw InvalidClassroomStaffRoleException(staffId)
        }

        if (
            assignmentRepository.existsByClassroomIdAndStaffId(
                classroomId,
                staffId
            )
        ) {
            throw StaffAssignmentAlreadyExistsException(
                staffId,
                classroomId
            )
        }

        val assignment = ClassroomStaffAssignment(
            classroomId = classroomId,
            staffId = staffId
        )

        return assignmentRepository
            .save(assignment)
            .toResponse()
    }

    @Transactional(readOnly = true)
    fun findByClassroom(
        classroomId: Long
    ): List<ClassroomStaffAssignmentResponse> {

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
                ?: throw StaffAssignmentNotFoundException(
                    staffId,
                    classroomId
                )

        assignmentRepository.delete(assignment)
    }

    private fun ClassroomStaffAssignment.toResponse() =
        ClassroomStaffAssignmentResponse(
            id = requireNotNull(id),
            classroomId = classroomId,
            staffId = staffId,
            assignedAt = assignedAt
        )
}