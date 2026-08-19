package com.issenur.brighttracker.meal

import java.time.LocalDate
import java.time.OffsetDateTime

data class StudentMealRecordResponse(
    val id: Long,
    val studentId: Long,
    val recordDate: LocalDate,
    val amSnackEaten: Boolean,
    val lunchEaten: Boolean,
    val pmSnackEaten: Boolean,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)