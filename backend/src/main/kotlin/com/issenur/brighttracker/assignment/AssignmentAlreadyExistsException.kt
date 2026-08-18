package com.issenur.brighttracker.assignment

class AssignmentAlreadyExistsException(
    staffId: Long,
    classroomId: Long
) : RuntimeException(
    "Staff member $staffId is already assigned to classroom $classroomId"
)