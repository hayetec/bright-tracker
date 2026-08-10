package com.issenur.brighttracker.enrollment

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "student_enrollments")
class StudentEnrollment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "student_id", nullable = false)
    var studentId: Long,

    @Column(name = "classroom_id", nullable = false)
    var classroomId: Long,

    @Column(
        name = "enrolled_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    var enrolledAt: OffsetDateTime? = null
)