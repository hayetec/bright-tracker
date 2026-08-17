package com.issenur.brighttracker.allergy

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class StudentAllergyUpdateRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val allergen: String,

    val severity: AllergySeverity,

    @field:Size(max = 500)
    val notes: String? = null
)