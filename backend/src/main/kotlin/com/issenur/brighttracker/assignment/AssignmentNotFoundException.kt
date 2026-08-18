package com.issenur.brighttracker.assignment

class AssignmentNotFoundException(
    staffId: Long,
    classroomId: Long
) : RuntimeException(
    "Staff member $staffId is not assigned to classroom $classroomId"
)