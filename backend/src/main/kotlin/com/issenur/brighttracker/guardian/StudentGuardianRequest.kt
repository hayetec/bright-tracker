package com.issenur.brighttracker.guardian

data class StudentGuardianRequest(
    val relationship: GuardianRelationship,
    val isPrimaryContact: Boolean = false,
    val isEmergencyContact: Boolean = false
)
