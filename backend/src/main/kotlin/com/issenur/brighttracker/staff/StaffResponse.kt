package com.issenur.brighttracker.staff

import java.time.OffsetDateTime

data class StaffResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phoneNumber: String?,
    val role: StaffRole,
    val status: StaffStatus,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)