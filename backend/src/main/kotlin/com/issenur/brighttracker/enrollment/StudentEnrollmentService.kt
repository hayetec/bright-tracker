package com.issenur.brighttracker.enrollment

import com.issenur.brighttracker.classroom.ClassroomNotFoundException
import com.issenur.brighttracker.classroom.ClassroomRepository
import com.issenur.brighttracker.student.StudentNotFoundException
import com.issenur.brighttracker.student.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudentEnrollmentService(
    private val studentEnrollmentRepository: StudentEnrollmentRepository,
    private val studentRepository: StudentRepository,
    private val classroomRepository: ClassroomRepository
) {

    @Transactional
    fun enroll(
        classroomId: Long,
        studentId: Long
    ): StudentEnrollmentResponse {

        if (!classroomRepository.existsById(classroomId)) {
            throw ClassroomNotFoundException(classroomId)
        }

        if (!studentRepository.existsById(studentId)) {
            throw StudentNotFoundException(studentId)
        }

        if (
            studentEnrollmentRepository.existsByStudentIdAndClassroomId(
                studentId,
                classroomId
            )
        ) {
            throw EnrollmentAlreadyExistsException(
                studentId,
                classroomId
            )
        }

        val enrollment = StudentEnrollment(
            studentId = studentId,
            classroomId = classroomId
        )

        return studentEnrollmentRepository
            .save(enrollment)
            .toResponse()
    }

    @Transactional(readOnly = true)
    fun findByClassroom(
        classroomId: Long
    ): List<StudentEnrollmentResponse> {

        if (!classroomRepository.existsById(classroomId)) {
            throw ClassroomNotFoundException(classroomId)
        }

        return studentEnrollmentRepository
            .findAllByClassroomId(classroomId)
            .map { it.toResponse() }
    }

    @Transactional
    fun remove(
        classroomId: Long,
        studentId: Long
    ) {
        val enrollment =
            studentEnrollmentRepository
                .findByStudentIdAndClassroomId(
                    studentId,
                    classroomId
                )
                ?: throw EnrollmentNotFoundException(
                    studentId,
                    classroomId
                )

        studentEnrollmentRepository.delete(enrollment)
    }

    private fun StudentEnrollment.toResponse() =
        StudentEnrollmentResponse(
            id = requireNotNull(id),
            studentId = studentId,
            classroomId = classroomId,
            enrolledAt = enrolledAt
        )
}