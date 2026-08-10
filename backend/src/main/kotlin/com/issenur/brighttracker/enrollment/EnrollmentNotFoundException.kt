package com.issenur.brighttracker.enrollment

class EnrollmentNotFoundException(
    studentId: Long,
    classroomId: Long
) : RuntimeException(
    "Student $studentId is not enrolled in classroom $classroomId"
)