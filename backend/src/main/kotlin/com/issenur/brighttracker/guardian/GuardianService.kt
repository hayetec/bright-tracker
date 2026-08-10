package com.issenur.brighttracker.guardian

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GuardianService(
    private val guardianRepository: GuardianRepository
) {

    @Transactional
    fun create(request: GuardianRequest): GuardianResponse {
        val guardian = Guardian(
            firstName = request.firstName,
            lastName = request.lastName,
            phoneNumber = request.phoneNumber,
            email = request.email
        )

        return guardianRepository
            .save(guardian)
            .toResponse()
    }

    @Transactional(readOnly = true)
    fun findAll(): List<GuardianResponse> =
        guardianRepository
            .findAll()
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findById(id: Long): GuardianResponse =
        findGuardian(id).toResponse()

    @Transactional
    fun update(
        id: Long,
        request: GuardianRequest
    ): GuardianResponse {

        val guardian = findGuardian(id)

        guardian.firstName = request.firstName
        guardian.lastName = request.lastName
        guardian.phoneNumber = request.phoneNumber
        guardian.email = request.email

        return guardianRepository
            .save(guardian)
            .toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        guardianRepository.delete(findGuardian(id))
    }

    private fun findGuardian(id: Long): Guardian =
        guardianRepository.findById(id)
            .orElseThrow { GuardianNotFoundException(id) }

    private fun Guardian.toResponse() =
        GuardianResponse(
            id = requireNotNull(id),
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            email = email,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}