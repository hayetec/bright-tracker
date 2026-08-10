package com.issenur.brighttracker.assignment

class StaffAssignmentAlreadyExistsException(
    staffId: Long,
    classroomId: Long
) : RuntimeException(
    "Staff member $staffId is already assigned to classroom $classroomId"
)