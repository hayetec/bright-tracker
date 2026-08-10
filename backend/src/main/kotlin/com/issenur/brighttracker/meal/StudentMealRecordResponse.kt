package com.issenur.brighttracker.meal

import java.time.LocalDate
import java.time.OffsetDateTime

data class StudentMealRecordResponse(
    val id: Long,
    val studentId: Long,
    val recordDate: LocalDate,
    val breakfastEaten: Boolean,
    val lunchEaten: Boolean,
    val dinnerEaten: Boolean,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)