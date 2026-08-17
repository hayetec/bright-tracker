package com.issenur.brighttracker.student

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class StudentRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val firstName: String,

    @field:NotBlank
    @field:Size(max = 100)
    val lastName: String,

    @field:Past
    val dateOfBirth: LocalDate,

    @field:NotBlank
    @field:Size(max = 30)
    val gradeLevel: String,

    val status: StudentStatus = StudentStatus.ACTIVE
)