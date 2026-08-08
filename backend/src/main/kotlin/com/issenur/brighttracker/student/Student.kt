package com.issenur.brighttracker.student

import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime

@Entity
@Table(name = "students")
class Student(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    @Column(name = "date_of_birth", nullable = false)
    var dateOfBirth: LocalDate,

    @Column(name = "grade_level", nullable = false)
    var gradeLevel: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: StudentStatus = StudentStatus.ACTIVE,

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    var updatedAt: OffsetDateTime? = null
)