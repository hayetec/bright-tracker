package com.issenur.brighttracker.meal

import com.issenur.brighttracker.allergy.StudentAllergyRepository
import com.issenur.brighttracker.classroom.ClassroomRepository
import com.issenur.brighttracker.enrollment.StudentEnrollmentRepository
import com.issenur.brighttracker.student.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class MealDashboardService(
    private val studentRepository: StudentRepository,
    private val mealRecordRepository: StudentMealRecordRepository,
    private val enrollmentRepository: StudentEnrollmentRepository,
    private val classroomRepository: ClassroomRepository,
    private val allergyRepository: StudentAllergyRepository,
) {

    @Transactional(readOnly = true)
    fun getDashboard(
        date: LocalDate
    ): MealDashboardResponse {

        val students = studentRepository.findAll()

        val mealRecordsByStudent =
            mealRecordRepository
                .findAllByRecordDate(date)
                .associateBy { it.studentId }

        val enrollmentsByStudent =
            enrollmentRepository
                .findAll()
                .associateBy { it.studentId }

        val classroomsById =
            classroomRepository
                .findAll()
                .associateBy { requireNotNull(it.id) }

        val studentIdsWithAllergies =
            allergyRepository
                .findAll()
                .map { it.studentId }
                .toSet()

        val dashboardStudents = students.map { student ->
            val studentId = requireNotNull(student.id)

            val mealRecord =
                mealRecordsByStudent[studentId]

            val enrollment =
                enrollmentsByStudent[studentId]

            val classroom =
                enrollment
                    ?.classroomId
                    ?.let { classroomsById[it] }

            MealDashboardStudentResponse(
                studentId = studentId,
                firstName = student.firstName,
                lastName = student.lastName,
                classroomId = classroom?.id,
                classroomName = classroom?.name,
                hasAllergies =
                    studentId in studentIdsWithAllergies,
                amSnackEaten =
                    mealRecord?.amSnackEaten ?: false,
                lunchEaten =
                    mealRecord?.lunchEaten ?: false,
                pmSnackEaten =
                    mealRecord?.pmSnackEaten ?: false,
            )
        }

        val totalStudents = dashboardStudents.size

        val amSnackEaten =
            dashboardStudents.count { it.amSnackEaten }

        val lunchEaten =
            dashboardStudents.count { it.lunchEaten }

        val pmSnackEaten =
            dashboardStudents.count { it.pmSnackEaten }

        return MealDashboardResponse(
            date = date,
            totalStudents = totalStudents,
            amSnack = MealProgressResponse(
                eaten = amSnackEaten,
                remaining = totalStudents - amSnackEaten,
            ),
            lunch = MealProgressResponse(
                eaten = lunchEaten,
                remaining = totalStudents - lunchEaten,
            ),
            pmSnack = MealProgressResponse(
                eaten = pmSnackEaten,
                remaining = totalStudents - pmSnackEaten,
            ),
            students = dashboardStudents,
        )
    }
}