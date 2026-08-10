package com.issenur.brighttracker.meal

import java.time.LocalDate

class StudentMealRecordNotFoundException(
    studentId: Long,
    recordDate: LocalDate
) : RuntimeException(
    "Meal record for student $studentId on $recordDate was not found"
)