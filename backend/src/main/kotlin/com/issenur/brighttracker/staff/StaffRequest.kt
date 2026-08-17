package com.issenur.brighttracker.staff

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class StaffRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val firstName: String,

    @field:NotBlank
    @field:Size(max = 100)
    val lastName: String,

    @field:Email
    @field:Size(max = 255)
    val email: String?,

    @field:Size(max = 30)
    val phoneNumber: String?,

    val role: StaffRole,

    val status: StaffStatus = StaffStatus.ACTIVE
)