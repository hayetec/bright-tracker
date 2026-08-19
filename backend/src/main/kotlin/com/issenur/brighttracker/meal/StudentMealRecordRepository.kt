package com.issenur.brighttracker.meal

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface StudentMealRecordRepository :
    JpaRepository<StudentMealRecord, Long> {

    fun findAllByStudentId(
        studentId: Long
    ): List<StudentMealRecord>

    fun findByStudentIdAndRecordDate(
        studentId: Long,
        recordDate: LocalDate
    ): StudentMealRecord?

    fun findAllByRecordDate(
        recordDate: LocalDate
    ): List<StudentMealRecord>

    fun existsByStudentIdAndRecordDate(
        studentId: Long,
        recordDate: LocalDate
    ): Boolean
}