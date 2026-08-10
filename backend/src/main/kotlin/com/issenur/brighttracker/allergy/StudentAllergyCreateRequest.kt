package com.issenur.brighttracker.allergy

data class StudentAllergyCreateRequest(
    val allergen: String,
    val severity: AllergySeverity,
    val notes: String? = null
)