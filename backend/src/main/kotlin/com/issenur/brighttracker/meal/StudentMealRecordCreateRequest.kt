package com.issenur.brighttracker.meal

import java.time.LocalDate

data class StudentMealRecordCreateRequest(
    val recordDate: LocalDate,
    val amSnackEaten: Boolean = false,
    val lunchEaten: Boolean = false,
    val pmSnackEaten: Boolean = false
)