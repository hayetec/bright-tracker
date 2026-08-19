package com.issenur.brighttracker.meal

import com.issenur.brighttracker.audit.AuditAction
import com.issenur.brighttracker.audit.AuditResourceType
import com.issenur.brighttracker.audit.AuditService
import com.issenur.brighttracker.student.StudentNotFoundException
import com.issenur.brighttracker.student.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class StudentMealRecordService(
    private val mealRecordRepository: StudentMealRecordRepository,
    private val studentRepository: StudentRepository,
    private val auditService: AuditService,
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
            amSnackEaten = request.amSnackEaten,
            lunchEaten = request.lunchEaten,
            pmSnackEaten = request.pmSnackEaten
        )

        val savedMealRecord =
            mealRecordRepository.save(mealRecord)

        auditService.record(
            action = AuditAction.CREATE,
            resourceType = AuditResourceType.STUDENT_MEAL_RECORD,
            resourceId = savedMealRecord.id,
        )

        return savedMealRecord.toResponse()
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

        mealRecord.amSnackEaten = request.amSnackEaten
        mealRecord.lunchEaten = request.lunchEaten
        mealRecord.pmSnackEaten = request.pmSnackEaten

        val savedMealRecord =
            mealRecordRepository.save(mealRecord)

        auditService.record(
            action = AuditAction.UPDATE,
            resourceType = AuditResourceType.STUDENT_MEAL_RECORD,
            resourceId = savedMealRecord.id,
        )

        return savedMealRecord.toResponse()
    }

    @Transactional
    fun delete(
        studentId: Long,
        recordDate: LocalDate
    ) {
        val mealRecord = findMealRecord(studentId, recordDate)
        val mealRecordId = requireNotNull(mealRecord.id)

        mealRecordRepository.delete(mealRecord)

        auditService.record(
            action = AuditAction.DELETE,
            resourceType = AuditResourceType.STUDENT_MEAL_RECORD,
            resourceId = mealRecordId,
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
            amSnackEaten = amSnackEaten,
            lunchEaten = lunchEaten,
            pmSnackEaten = pmSnackEaten,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}