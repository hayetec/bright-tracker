package com.issenur.brighttracker.guardian

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class StudentGuardianResponse(
    val id: Long,
    val studentId: Long,
    val guardianId: Long,
    val relationship: GuardianRelationship,

    @get:JsonProperty("isPrimaryContact")
    val isPrimaryContact: Boolean,

    @get:JsonProperty("isEmergencyContact")
    val isEmergencyContact: Boolean,

    val createdAt: OffsetDateTime?
)