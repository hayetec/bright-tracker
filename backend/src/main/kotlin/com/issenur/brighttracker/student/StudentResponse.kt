package com.issenur.brighttracker.student

import java.time.LocalDate
import java.time.OffsetDateTime

data class StudentResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: LocalDate,
    val gradeLevel: String,
    val status: StudentStatus,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)