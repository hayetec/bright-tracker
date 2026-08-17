package com.issenur.brighttracker.classroom

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ClassroomRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    @field:NotBlank
    @field:Size(max = 50)
    val gradeLevel: String,

    @field:Size(max = 50)
    val roomNumber: String?,

    @field:Min(1)
    val capacity: Int?,

    val status: ClassroomStatus = ClassroomStatus.ACTIVE
)