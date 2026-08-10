package com.issenur.brighttracker.guardian

import java.time.OffsetDateTime

data class GuardianResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String?,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)