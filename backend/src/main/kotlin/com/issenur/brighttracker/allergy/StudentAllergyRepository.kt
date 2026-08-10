package com.issenur.brighttracker.allergy

import org.springframework.data.jpa.repository.JpaRepository

interface StudentAllergyRepository :
    JpaRepository<StudentAllergy, Long> {

    fun findAllByStudentId(
        studentId: Long
    ): List<StudentAllergy>

    fun findByStudentIdAndId(
        studentId: Long,
        id: Long
    ): StudentAllergy?

    fun existsByStudentIdAndAllergenIgnoreCase(
        studentId: Long,
        allergen: String
    ): Boolean
}