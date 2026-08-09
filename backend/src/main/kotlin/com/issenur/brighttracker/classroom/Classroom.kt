package com.issenur.brighttracker.classroom

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "classrooms")
class Classroom(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(name = "grade_level", nullable = false)
    var gradeLevel: String,

    @Column(name = "room_number")
    var roomNumber: String? = null,

    var capacity: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ClassroomStatus = ClassroomStatus.ACTIVE,

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    var updatedAt: OffsetDateTime? = null
)