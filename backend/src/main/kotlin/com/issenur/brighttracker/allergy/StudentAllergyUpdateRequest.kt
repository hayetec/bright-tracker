package com.issenur.brighttracker.allergy

data class StudentAllergyUpdateRequest(
    val allergen: String,
    val severity: AllergySeverity,
    val notes: String? = null
)