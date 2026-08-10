package com.issenur.brighttracker.guardian

class StudentGuardianAlreadyExistsException(
    studentId: Long,
    guardianId: Long
) : RuntimeException(
    "Guardian $guardianId is already linked to student $studentId"
)