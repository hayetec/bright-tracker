package com.issenur.brighttracker.allergy

import java.time.OffsetDateTime

data class StudentAllergyResponse(
    val id: Long,
    val studentId: Long,
    val allergen: String,
    val severity: AllergySeverity,
    val notes: String?,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)