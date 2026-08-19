package com.issenur.brighttracker.meal

import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime

@Entity
@Table(name = "student_meal_records")
class StudentMealRecord(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "student_id", nullable = false)
    var studentId: Long,

    @Column(name = "record_date", nullable = false)
    var recordDate: LocalDate,

    @Column(name = "am_snack_eaten", nullable = false)
    var amSnackEaten: Boolean = false,

    @Column(name = "lunch_eaten", nullable = false)
    var lunchEaten: Boolean = false,

    @Column(name = "pm_snack_eaten", nullable = false)
    var pmSnackEaten: Boolean = false,

    @Column(
        name = "created_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    var createdAt: OffsetDateTime? = null,

    @Column(
        name = "updated_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    var updatedAt: OffsetDateTime? = null
)