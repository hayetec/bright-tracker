package com.issenur.brighttracker.assignment

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "classroom_staff_assignments")
class StaffAssignment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "classroom_id", nullable = false)
    var classroomId: Long,

    @Column(name = "staff_id", nullable = false)
    var staffId: Long,

    @Column(
        name = "assigned_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    var assignedAt: OffsetDateTime? = null
)