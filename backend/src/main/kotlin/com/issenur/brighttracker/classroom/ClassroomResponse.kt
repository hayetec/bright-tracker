package com.issenur.brighttracker.classroom

import java.time.OffsetDateTime

data class ClassroomResponse(
    val id: Long,
    val name: String,
    val gradeLevel: String,
    val roomNumber: String?,
    val capacity: Int?,
    val status: ClassroomStatus,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)
