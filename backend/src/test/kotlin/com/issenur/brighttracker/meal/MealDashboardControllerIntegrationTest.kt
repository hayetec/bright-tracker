package com.issenur.brighttracker.meal

import com.issenur.brighttracker.student.StudentRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class MealDashboardControllerIntegrationTest {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var mealRecordRepository: StudentMealRecordRepository

    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mealRecordRepository.deleteAll()
        studentRepository.deleteAll()

        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultRequest<DefaultMockMvcBuilder>(
                MockMvcRequestBuilders.get("/")
                    .with(
                        jwt()
                            .jwt { jwt ->
                                jwt
                                    .subject("test-admin-subject")
                                    .claim(
                                        "preferred_username",
                                        "test-admin"
                                    )
                            }
                            .authorities(
                                SimpleGrantedAuthority("ROLE_ADMIN")
                            )
                    )
            )
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    companion object {

        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16")
            .withDatabaseName("bright_tracker_test")
            .withUsername("bright_tracker")
            .withPassword("bright_tracker")

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(
            registry: DynamicPropertyRegistry
        ) {
            registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl
            )
            registry.add(
                "spring.datasource.username",
                postgres::getUsername
            )
            registry.add(
                "spring.datasource.password",
                postgres::getPassword
            )
        }
    }

    @Test
    fun `gets meal dashboard for date`() {
        val studentId = createStudent()

        createMealRecord(
            studentId = studentId,
            amSnackEaten = true,
            lunchEaten = false,
            pmSnackEaten = false
        )

        mockMvc.get(
            "/api/meal-dashboard?date=2026-08-19"
        )
            .andExpect {
                status { isOk() }

                jsonPath("$.date") {
                    value("2026-08-19")
                }

                jsonPath("$.totalStudents") {
                    value(1)
                }

                jsonPath("$.amSnack.eaten") {
                    value(1)
                }

                jsonPath("$.amSnack.remaining") {
                    value(0)
                }

                jsonPath("$.lunch.eaten") {
                    value(0)
                }

                jsonPath("$.lunch.remaining") {
                    value(1)
                }

                jsonPath("$.pmSnack.eaten") {
                    value(0)
                }

                jsonPath("$.pmSnack.remaining") {
                    value(1)
                }

                jsonPath("$.students[0].studentId") {
                    value(studentId)
                }

                jsonPath("$.students[0].firstName") {
                    value("Amina")
                }

                jsonPath("$.students[0].lastName") {
                    value("Ahmed")
                }

                jsonPath("$.students[0].amSnackEaten") {
                    value(true)
                }

                jsonPath("$.students[0].lunchEaten") {
                    value(false)
                }

                jsonPath("$.students[0].pmSnackEaten") {
                    value(false)
                }
            }
    }

    @Test
    fun `treats missing meal record as not eaten`() {
        val studentId = createStudent()

        mockMvc.get(
            "/api/meal-dashboard?date=2026-08-19"
        )
            .andExpect {
                status { isOk() }

                jsonPath("$.totalStudents") {
                    value(1)
                }

                jsonPath("$.amSnack.eaten") {
                    value(0)
                }

                jsonPath("$.amSnack.remaining") {
                    value(1)
                }

                jsonPath("$.lunch.eaten") {
                    value(0)
                }

                jsonPath("$.lunch.remaining") {
                    value(1)
                }

                jsonPath("$.pmSnack.eaten") {
                    value(0)
                }

                jsonPath("$.pmSnack.remaining") {
                    value(1)
                }

                jsonPath("$.students[0].studentId") {
                    value(studentId)
                }

                jsonPath("$.students[0].amSnackEaten") {
                    value(false)
                }

                jsonPath("$.students[0].lunchEaten") {
                    value(false)
                }

                jsonPath("$.students[0].pmSnackEaten") {
                    value(false)
                }
            }
    }

    @Test
    fun `includes classroom and allergy information for student`() {
        val studentId = createStudent()
        val classroomId = createClassroom()

        enrollStudent(
            studentId = studentId,
            classroomId = classroomId
        )

        createAllergy(studentId)

        mockMvc.get(
            "/api/meal-dashboard?date=2026-08-19"
        )
            .andExpect {
                status { isOk() }

                jsonPath("$.totalStudents") {
                    value(1)
                }

                jsonPath("$.students[0].studentId") {
                    value(studentId)
                }

                jsonPath("$.students[0].classroomId") {
                    value(classroomId)
                }

                jsonPath("$.students[0].classroomName") {
                    value("Room A")
                }

                jsonPath("$.students[0].hasAllergies") {
                    value(true)
                }
            }
    }

    private fun createMealRecord(
        studentId: Long,
        amSnackEaten: Boolean,
        lunchEaten: Boolean,
        pmSnackEaten: Boolean
    ) {
        mockMvc.post(
            "/api/students/$studentId/meals"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "recordDate": "2026-08-19",
                  "amSnackEaten": $amSnackEaten,
                  "lunchEaten": $lunchEaten,
                  "pmSnackEaten": $pmSnackEaten
                }
            """.trimIndent()
        }
            .andExpect {
                status { isCreated() }
            }
    }

    private fun createStudent(): Long {
        val result = mockMvc.post("/api/students") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "firstName": "Amina",
                  "lastName": "Ahmed",
                  "dateOfBirth": "2018-05-14",
                  "gradeLevel": "1",
                  "status": "ACTIVE"
                }
            """.trimIndent()
        }
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        return extractId(
            result.response.contentAsString
        )
    }

    private fun createClassroom(): Long {
        val result = mockMvc.post("/api/classrooms") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "name": "Room A",
              "gradeLevel": "1",
              "roomNumber": "101",
              "capacity": 20,
              "status": "ACTIVE"
            }
        """.trimIndent()
        }
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        return extractId(
            result.response.contentAsString
        )
    }

    private fun enrollStudent(
        studentId: Long,
        classroomId: Long
    ) {
        mockMvc.post(
            "/api/classrooms/$classroomId/students/$studentId"
        )
            .andExpect {
                status { isCreated() }
            }
    }

    private fun createAllergy(
        studentId: Long
    ) {
        mockMvc.post(
            "/api/students/$studentId/allergies"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "allergen": "Peanuts",
              "severity": "SEVERE",
              "notes": "Carries epinephrine"
            }
        """.trimIndent()
        }
            .andExpect {
                status { isCreated() }
            }
    }

    private fun extractId(response: String): Long =
        Regex(""""id":(\d+)""")
            .find(response)
            ?.groupValues
            ?.get(1)
            ?.toLong()
            ?: error("ID was not returned")



}