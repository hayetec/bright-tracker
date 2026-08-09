package com.issenur.brighttracker.staff

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "staff")
class Staff(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    @Column(unique = true)
    var email: String? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: StaffRole,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: StaffStatus = StaffStatus.ACTIVE,

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    var updatedAt: OffsetDateTime? = null
)