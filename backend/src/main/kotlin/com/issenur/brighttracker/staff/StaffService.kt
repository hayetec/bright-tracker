package com.issenur.brighttracker.staff

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StaffService(
    private val staffRepository: StaffRepository
) {

    @Transactional
    fun create(request: StaffRequest): StaffResponse {
        val staff = Staff(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            phoneNumber = request.phoneNumber,
            role = request.role,
            status = request.status
        )

        return staffRepository.save(staff).toResponse()
    }

    @Transactional(readOnly = true)
    fun findAll(): List<StaffResponse> =
        staffRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findById(id: Long): StaffResponse =
        findStaff(id).toResponse()

    @Transactional
    fun update(id: Long, request: StaffRequest): StaffResponse {
        val staff = findStaff(id)

        staff.firstName = request.firstName
        staff.lastName = request.lastName
        staff.email = request.email
        staff.phoneNumber = request.phoneNumber
        staff.role = request.role
        staff.status = request.status

        return staffRepository.save(staff).toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        staffRepository.delete(findStaff(id))
    }

    private fun findStaff(id: Long): Staff =
        staffRepository.findById(id)
            .orElseThrow { StaffNotFoundException(id) }

    private fun Staff.toResponse() =
        StaffResponse(
            id = requireNotNull(id),
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber,
            role = role,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}