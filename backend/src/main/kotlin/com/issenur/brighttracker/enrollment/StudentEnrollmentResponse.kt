package com.issenur.brighttracker.enrollment

import java.time.OffsetDateTime

data class StudentEnrollmentResponse(
    val id: Long,
    val studentId: Long,
    val classroomId: Long,
    val enrolledAt: OffsetDateTime?
)