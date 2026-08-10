package com.issenur.brighttracker.guardian

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "guardians")
class Guardian(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    @Column(name = "phone_number", nullable = false)
    var phoneNumber: String,

    @Column(name = "email")
    var email: String? = null,

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