package com.issenur.brighttracker.meal

import com.issenur.brighttracker.student.StudentNotFoundException
import com.issenur.brighttracker.student.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class StudentMealRecordService(
    private val mealRecordRepository: StudentMealRecordRepository,
    private val studentRepository: StudentRepository
) {

    @Transactional
    fun create(
        studentId: Long,
        request: StudentMealRecordCreateRequest
    ): StudentMealRecordResponse {

        if (!studentRepository.existsById(studentId)) {
            throw StudentNotFoundException(studentId)
        }

        if (
            mealRecordRepository.existsByStudentIdAndRecordDate(
                studentId,
                request.recordDate
            )
        ) {
            throw StudentMealRecordAlreadyExistsException(
                studentId,
                request.recordDate
            )
        }

        val mealRecord = StudentMealRecord(
            studentId = studentId,
            recordDate = request.recordDate,
            breakfastEaten = request.breakfastEaten,
            lunchEaten = request.lunchEaten,
            dinnerEaten = request.dinnerEaten
        )

        return mealRecordRepository
            .save(mealRecord)
            .toResponse()
    }

    @Transactional(readOnly = true)
    fun findAllByStudent(
        studentId: Long
    ): List<StudentMealRecordResponse> {

        if (!studentRepository.existsById(studentId)) {
            throw StudentNotFoundException(studentId)
        }

        return mealRecordRepository
            .findAllByStudentId(studentId)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun findByStudentAndDate(
        studentId: Long,
        recordDate: LocalDate
    ): StudentMealRecordResponse =
        findMealRecord(studentId, recordDate)
            .toResponse()

    @Transactional
    fun update(
        studentId: Long,
        recordDate: LocalDate,
        request: StudentMealRecordUpdateRequest
    ): StudentMealRecordResponse {

        val mealRecord = findMealRecord(
            studentId,
            recordDate
        )

        mealRecord.breakfastEaten = request.breakfastEaten
        mealRecord.lunchEaten = request.lunchEaten
        mealRecord.dinnerEaten = request.dinnerEaten

        return mealRecordRepository
            .save(mealRecord)
            .toResponse()
    }

    @Transactional
    fun delete(
        studentId: Long,
        recordDate: LocalDate
    ) {
        mealRecordRepository.delete(
            findMealRecord(studentId, recordDate)
        )
    }

    private fun findMealRecord(
        studentId: Long,
        recordDate: LocalDate
    ): StudentMealRecord =
        mealRecordRepository
            .findByStudentIdAndRecordDate(
                studentId,
                recordDate
            )
            ?: throw StudentMealRecordNotFoundException(
                studentId,
                recordDate
            )

    private fun StudentMealRecord.toResponse() =
        StudentMealRecordResponse(
            id = requireNotNull(id),
            studentId = studentId,
            recordDate = recordDate,
            breakfastEaten = breakfastEaten,
            lunchEaten = lunchEaten,
            dinnerEaten = dinnerEaten,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}