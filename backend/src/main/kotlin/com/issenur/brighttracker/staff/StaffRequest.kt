package com.issenur.brighttracker.staff

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class StaffRequest(
    @field:NotBlank
    val firstName: String,

    @field:NotBlank
    val lastName: String,

    @field:Email
    val email: String?,

    val phoneNumber: String?,

    @field:NotNull
    val role: StaffRole,

    val status: StaffStatus = StaffStatus.ACTIVE
)