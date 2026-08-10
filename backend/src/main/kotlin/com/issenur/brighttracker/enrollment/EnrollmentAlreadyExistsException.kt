package com.issenur.brighttracker.enrollment

class EnrollmentAlreadyExistsException(
    studentId: Long,
    classroomId: Long
) : RuntimeException(
    "Student $studentId is already enrolled in classroom $classroomId"
)