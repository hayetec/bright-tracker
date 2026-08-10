package com.issenur.brighttracker.assignment

import org.springframework.data.jpa.repository.JpaRepository

interface ClassroomStaffAssignmentRepository :
    JpaRepository<ClassroomStaffAssignment, Long> {

    fun findAllByClassroomId(
        classroomId: Long
    ): List<ClassroomStaffAssignment>

    fun findByClassroomIdAndStaffId(
        classroomId: Long,
        staffId: Long
    ): ClassroomStaffAssignment?

    fun existsByClassroomIdAndStaffId(
        classroomId: Long,
        staffId: Long
    ): Boolean
}