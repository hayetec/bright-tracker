package com.issenur.brighttracker.guardian

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class GuardianRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val firstName: String,

    @field:NotBlank
    @field:Size(max = 100)
    val lastName: String,

    @field:NotBlank
    @field:Size(max = 30)
    val phoneNumber: String,

    @field:Email
    @field:Size(max = 255)
    val email: String? = null
)