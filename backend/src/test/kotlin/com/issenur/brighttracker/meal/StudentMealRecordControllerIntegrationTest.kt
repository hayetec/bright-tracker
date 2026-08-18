package com.issenur.brighttracker.meal

import com.issenur.brighttracker.audit.AuditAction
import com.issenur.brighttracker.audit.AuditLogRepository
import com.issenur.brighttracker.audit.AuditResourceType
import org.junit.jupiter.api.Assertions.assertEquals
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
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
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
class StudentMealRecordControllerIntegrationTest {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var auditLogRepository: AuditLogRepository

    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUpMockMvc() {
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
    fun `creates a meal record`() {
        val studentId = createStudent()

        mockMvc.post("/api/students/$studentId/meals") {
            contentType = MediaType.APPLICATION_JSON
            content = createRequest()
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.id") { exists() }
                jsonPath("$.studentId") { value(studentId) }
                jsonPath("$.recordDate") {
                    value("2026-08-16")
                }
                jsonPath("$.breakfastEaten") {
                    value(true)
                }
                jsonPath("$.lunchEaten") {
                    value(false)
                }
                jsonPath("$.dinnerEaten") {
                    value(true)
                }
            }
    }

    @Test
    fun `gets all meal records for a student`() {
        val studentId = createStudent()
        createMealRecord(studentId)

        mockMvc.get("/api/students/$studentId/meals")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].studentId") {
                    value(studentId)
                }
                jsonPath("$[0].recordDate") {
                    value("2026-08-16")
                }
            }
    }

    @Test
    fun `gets meal record by date`() {
        val studentId = createStudent()
        createMealRecord(studentId)

        mockMvc.get(
            "/api/students/$studentId/meals/2026-08-16"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$.studentId") {
                    value(studentId)
                }
                jsonPath("$.recordDate") {
                    value("2026-08-16")
                }
            }
    }

    @Test
    fun `updates a meal record`() {
        val studentId = createStudent()
        createMealRecord(studentId)

        mockMvc.put(
            "/api/students/$studentId/meals/2026-08-16"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "breakfastEaten": false,
                  "lunchEaten": true,
                  "dinnerEaten": false
                }
            """.trimIndent()
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.breakfastEaten") {
                    value(false)
                }
                jsonPath("$.lunchEaten") {
                    value(true)
                }
                jsonPath("$.dinnerEaten") {
                    value(false)
                }
            }
    }

    @Test
    fun `deletes a meal record`() {
        val studentId = createStudent()
        createMealRecord(studentId)

        mockMvc.delete(
            "/api/students/$studentId/meals/2026-08-16"
        )
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get(
            "/api/students/$studentId/meals/2026-08-16"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `rejects duplicate meal record`() {
        val studentId = createStudent()
        createMealRecord(studentId)

        mockMvc.post("/api/students/$studentId/meals") {
            contentType = MediaType.APPLICATION_JSON
            content = createRequest()
        }
            .andExpect {
                status { isConflict() }
            }
    }

    @Test
    fun `returns 404 when student does not exist`() {
        mockMvc.post("/api/students/999999/meals") {
            contentType = MediaType.APPLICATION_JSON
            content = createRequest()
        }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns 404 when meal record does not exist`() {
        val studentId = createStudent()

        mockMvc.get(
            "/api/students/$studentId/meals/2026-08-16"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `creates audit log when meal record is created`() {
        val studentId = createStudent()

        val result = mockMvc.post(
            "/api/students/$studentId/meals"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = createRequest()
        }
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        val mealRecordId =
            extractId(result.response.contentAsString)

        val auditLog = auditLogRepository.findAll()
            .filter {
                it.resourceType ==
                        AuditResourceType.STUDENT_MEAL_RECORD &&
                        it.resourceId == mealRecordId
            }
            .last { it.action == AuditAction.CREATE }

        assertEquals(
            AuditAction.CREATE,
            auditLog.action
        )
        assertEquals(
            AuditResourceType.STUDENT_MEAL_RECORD,
            auditLog.resourceType
        )
        assertEquals(
            mealRecordId,
            auditLog.resourceId
        )
        assertEquals(
            "test-admin-subject",
            auditLog.actorSubject
        )
        assertEquals(
            "test-admin",
            auditLog.actorUsername
        )
    }

    @Test
    fun `creates audit log when meal record is updated`() {
        val studentId = createStudent()
        val mealRecordId = createMealRecord(studentId)

        mockMvc.put(
            "/api/students/$studentId/meals/2026-08-16"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "breakfastEaten": false,
                  "lunchEaten": true,
                  "dinnerEaten": false
                }
            """.trimIndent()
        }
            .andExpect {
                status { isOk() }
            }

        val auditLog = auditLogRepository.findAll()
            .filter {
                it.resourceType ==
                        AuditResourceType.STUDENT_MEAL_RECORD &&
                        it.resourceId == mealRecordId
            }
            .last { it.action == AuditAction.UPDATE }

        assertEquals(
            AuditAction.UPDATE,
            auditLog.action
        )
        assertEquals(
            AuditResourceType.STUDENT_MEAL_RECORD,
            auditLog.resourceType
        )
        assertEquals(
            mealRecordId,
            auditLog.resourceId
        )
        assertEquals(
            "test-admin-subject",
            auditLog.actorSubject
        )
        assertEquals(
            "test-admin",
            auditLog.actorUsername
        )
    }

    @Test
    fun `creates audit log when meal record is deleted`() {
        val studentId = createStudent()
        val mealRecordId = createMealRecord(studentId)

        mockMvc.delete(
            "/api/students/$studentId/meals/2026-08-16"
        )
            .andExpect {
                status { isNoContent() }
            }

        val auditLog = auditLogRepository.findAll()
            .filter {
                it.resourceType ==
                        AuditResourceType.STUDENT_MEAL_RECORD &&
                        it.resourceId == mealRecordId
            }
            .last { it.action == AuditAction.DELETE }

        assertEquals(
            AuditAction.DELETE,
            auditLog.action
        )
        assertEquals(
            AuditResourceType.STUDENT_MEAL_RECORD,
            auditLog.resourceType
        )
        assertEquals(
            mealRecordId,
            auditLog.resourceId
        )
        assertEquals(
            "test-admin-subject",
            auditLog.actorSubject
        )
        assertEquals(
            "test-admin",
            auditLog.actorUsername
        )
    }

    private fun createMealRecord(
        studentId: Long
    ): Long {
        val result = mockMvc.post(
            "/api/students/$studentId/meals"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = createRequest()
        }
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        return extractId(
            result.response.contentAsString
        )
    }

    private fun createRequest(): String =
        """
            {
              "recordDate": "2026-08-16",
              "breakfastEaten": true,
              "lunchEaten": false,
              "dinnerEaten": true
            }
        """.trimIndent()

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

    private fun extractId(response: String): Long =
        Regex(""""id":(\d+)""")
            .find(response)
            ?.groupValues
            ?.get(1)
            ?.toLong()
            ?: error("ID was not returned")
}