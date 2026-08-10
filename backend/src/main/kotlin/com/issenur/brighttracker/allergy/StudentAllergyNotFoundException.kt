package com.issenur.brighttracker.allergy

class StudentAllergyNotFoundException(
    studentId: Long,
    allergyId: Long
) : RuntimeException(
    "Allergy $allergyId for student $studentId was not found"
)