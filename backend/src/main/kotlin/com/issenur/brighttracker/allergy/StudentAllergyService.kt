package com.issenur.brighttracker.allergy

import com.issenur.brighttracker.student.StudentNotFoundException
import com.issenur.brighttracker.student.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudentAllergyService(
    private val allergyRepository: StudentAllergyRepository,
    private val studentRepository: StudentRepository
) {

    @Transactional
    fun create(
        studentId: Long,
        request: StudentAllergyCreateRequest
    ): StudentAllergyResponse {

        if (!studentRepository.existsById(studentId)) {
            throw StudentNotFoundException(studentId)
        }

        val allergen = request.allergen.trim()

        if (
            allergyRepository.existsByStudentIdAndAllergenIgnoreCase(
                studentId,
                allergen
            )
        ) {
            throw StudentAllergyAlreadyExistsException(
                studentId,
                allergen
            )
        }

        val allergy = StudentAllergy(
            studentId = studentId,
            allergen = allergen,
            severity = request.severity,
            notes = request.notes
        )

        return allergyRepository
            .save(allergy)
            .toResponse()
    }

    @Transactional(readOnly = true)
    fun findAllByStudent(
        studentId: Long
    ): List<StudentAllergyResponse> {

        if (!studentRepository.existsById(studentId)) {
            throw StudentNotFoundException(studentId)
        }

        return allergyRepository
            .findAllByStudentId(studentId)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun findById(
        studentId: Long,
        allergyId: Long
    ): StudentAllergyResponse =
        findAllergy(studentId, allergyId)
            .toResponse()

    @Transactional
    fun update(
        studentId: Long,
        allergyId: Long,
        request: StudentAllergyUpdateRequest
    ): StudentAllergyResponse {

        val allergy = findAllergy(
            studentId,
            allergyId
        )

        val allergen = request.allergen.trim()

        val duplicateExists =
            allergy.allergen.equals(allergen, ignoreCase = true).not() &&
                    allergyRepository.existsByStudentIdAndAllergenIgnoreCase(
                        studentId,
                        allergen
                    )

        if (duplicateExists) {
            throw StudentAllergyAlreadyExistsException(
                studentId,
                allergen
            )
        }

        allergy.allergen = allergen
        allergy.severity = request.severity
        allergy.notes = request.notes

        return allergyRepository
            .save(allergy)
            .toResponse()
    }

    @Transactional
    fun delete(
        studentId: Long,
        allergyId: Long
    ) {
        allergyRepository.delete(
            findAllergy(studentId, allergyId)
        )
    }

    private fun findAllergy(
        studentId: Long,
        allergyId: Long
    ): StudentAllergy =
        allergyRepository
            .findByStudentIdAndId(
                studentId,
                allergyId
            )
            ?: throw StudentAllergyNotFoundException(
                studentId,
                allergyId
            )

    private fun StudentAllergy.toResponse() =
        StudentAllergyResponse(
            id = requireNotNull(id),
            studentId = studentId,
            allergen = allergen,
            severity = severity,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}