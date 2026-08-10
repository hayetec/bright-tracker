package com.issenur.brighttracker.guardian

import org.springframework.data.jpa.repository.JpaRepository

interface StudentGuardianRepository :
    JpaRepository<StudentGuardian, Long> {

    fun findAllByStudentId(
        studentId: Long
    ): List<StudentGuardian>

    fun findAllByGuardianId(
        guardianId: Long
    ): List<StudentGuardian>

    fun findByStudentIdAndGuardianId(
        studentId: Long,
        guardianId: Long
    ): StudentGuardian?

    fun existsByStudentIdAndGuardianId(
        studentId: Long,
        guardianId: Long
    ): Boolean
}