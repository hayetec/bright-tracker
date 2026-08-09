package com.issenur.brighttracker.classroom

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClassroomService(
    private val classroomRepository: ClassroomRepository
) {

    @Transactional
    fun create(request: ClassroomRequest): ClassroomResponse {
        val classroom = Classroom(
            name = request.name,
            gradeLevel = request.gradeLevel,
            roomNumber = request.roomNumber,
            capacity = request.capacity,
            status = request.status
        )

        return classroomRepository.save(classroom).toResponse()
    }

    @Transactional(readOnly = true)
    fun findAll(): List<ClassroomResponse> =
        classroomRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findById(id: Long): ClassroomResponse =
        findClassroom(id).toResponse()

    @Transactional
    fun update(
        id: Long,
        request: ClassroomRequest
    ): ClassroomResponse {
        val classroom = findClassroom(id)

        classroom.name = request.name
        classroom.gradeLevel = request.gradeLevel
        classroom.roomNumber = request.roomNumber
        classroom.capacity = request.capacity
        classroom.status = request.status

        return classroomRepository.save(classroom).toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        classroomRepository.delete(findClassroom(id))
    }

    private fun findClassroom(id: Long): Classroom =
        classroomRepository.findById(id)
            .orElseThrow { ClassroomNotFoundException(id) }

    private fun Classroom.toResponse() =
        ClassroomResponse(
            id = requireNotNull(id),
            name = name,
            gradeLevel = gradeLevel,
            roomNumber = roomNumber,
            capacity = capacity,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}