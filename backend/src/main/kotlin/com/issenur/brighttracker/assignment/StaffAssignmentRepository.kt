package com.issenur.brighttracker.assignment

import org.springframework.data.jpa.repository.JpaRepository

interface StaffAssignmentRepository :
    JpaRepository<StaffAssignment, Long> {

    fun findAllByClassroomId(
        classroomId: Long
    ): List<StaffAssignment>

    fun findByClassroomIdAndStaffId(
        classroomId: Long,
        staffId: Long
    ): StaffAssignment?

    fun existsByClassroomIdAndStaffId(
        classroomId: Long,
        staffId: Long
    ): Boolean
}