package com.issenur.brighttracker.meal

import java.time.LocalDate

class StudentMealRecordAlreadyExistsException(
    studentId: Long,
    recordDate: LocalDate
) : RuntimeException(
    "Meal record for student $studentId on $recordDate already exists"
)