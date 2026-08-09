package com.issenur.brighttracker.classroom

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class ClassroomRequest(

    @field:NotBlank
    val name: String,

    @field:NotBlank
    val gradeLevel: String,

    val roomNumber: String?,

    @field:Min(1)
    val capacity: Int?,

    val status: ClassroomStatus = ClassroomStatus.ACTIVE
)