package com.issenur.brighttracker.enrollment

import org.springframework.data.jpa.repository.JpaRepository

interface StudentEnrollmentRepository :
    JpaRepository<StudentEnrollment, Long> {

    fun findAllByClassroomId(
        classroomId: Long
    ): List<StudentEnrollment>

    fun findByStudentIdAndClassroomId(
        studentId: Long,
        classroomId: Long
    ): StudentEnrollment?

    fun existsByStudentIdAndClassroomId(
        studentId: Long,
        classroomId: Long
    ): Boolean
}