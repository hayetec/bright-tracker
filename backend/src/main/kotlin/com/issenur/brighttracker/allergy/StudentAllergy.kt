package com.issenur.brighttracker.allergy

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "student_allergies")
class StudentAllergy(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "student_id", nullable = false)
    var studentId: Long,

    @Column(name = "allergen", nullable = false)
    var allergen: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    var severity: AllergySeverity,

    @Column(name = "notes")
    var notes: String? = null,

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