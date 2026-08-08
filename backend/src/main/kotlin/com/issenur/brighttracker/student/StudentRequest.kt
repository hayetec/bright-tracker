package com.issenur.brighttracker.student

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class StudentRequest(
    @field:NotBlank
    val firstName: String,

    @field:NotBlank
    val lastName: String,

    @field:NotNull
    val dateOfBirth: LocalDate,

    @field:NotBlank
    val gradeLevel: String,

    val status: StudentStatus = StudentStatus.ACTIVE
)