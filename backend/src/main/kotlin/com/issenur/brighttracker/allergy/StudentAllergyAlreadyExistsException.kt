package com.issenur.brighttracker.allergy

class StudentAllergyAlreadyExistsException(
    studentId: Long,
    allergen: String
) : RuntimeException(
    "Student $studentId already has an allergy recorded for $allergen"
)