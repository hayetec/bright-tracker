package com.issenur.brighttracker.guardian

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "student_guardians")
class StudentGuardian(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "student_id", nullable = false)
    var studentId: Long,

    @Column(name = "guardian_id", nullable = false)
    var guardianId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship", nullable = false)
    var relationship: GuardianRelationship,

    @Column(name = "is_primary_contact", nullable = false)
    var isPrimaryContact: Boolean = false,

    @Column(name = "is_emergency_contact", nullable = false)
    var isEmergencyContact: Boolean = false,

    @Column(
        name = "created_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    var createdAt: OffsetDateTime? = null
)