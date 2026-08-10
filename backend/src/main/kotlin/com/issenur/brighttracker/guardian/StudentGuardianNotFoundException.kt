package com.issenur.brighttracker.guardian

class StudentGuardianNotFoundException(
    studentId: Long,
    guardianId: Long
) : RuntimeException(
    "Guardian $guardianId is not linked to student $studentId"
)