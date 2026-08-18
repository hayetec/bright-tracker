package com.issenur.brighttracker.assignment

class AssignmentInvalidRoleException(
    staffId: Long
) : RuntimeException(
    "Staff member $staffId must be a TEACHER or TEACHER_AIDE to be assigned to a classroom"
)