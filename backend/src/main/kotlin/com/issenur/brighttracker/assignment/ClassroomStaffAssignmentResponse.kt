package com.issenur.brighttracker.assignment

import java.time.OffsetDateTime

data class ClassroomStaffAssignmentResponse(
    val id: Long,
    val classroomId: Long,
    val staffId: Long,
    val assignedAt: OffsetDateTime?
)
