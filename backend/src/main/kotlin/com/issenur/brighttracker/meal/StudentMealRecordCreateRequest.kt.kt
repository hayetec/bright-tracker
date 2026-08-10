package com.issenur.brighttracker.meal

import java.time.LocalDate

data class StudentMealRecordCreateRequest(
    val recordDate: LocalDate,
    val breakfastEaten: Boolean = false,
    val lunchEaten: Boolean = false,
    val dinnerEaten: Boolean = false
)