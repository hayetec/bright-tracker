package com.issenur.brighttracker.enrollment

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
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
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
@WithMockUser(roles = ["ADMIN"])
class StudentEnrollmentControllerIntegrationTest {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var auditLogRepository: AuditLogRepository

    @Autowired
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
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @Test
    fun `enrolls student in classroom`() {
        val studentId = createStudent()
        val classroomId = createClassroom()

        mockMvc.post(
            "/api/classrooms/$classroomId/students/$studentId"
        )
            .andExpect {
                status { isCreated() }
                jsonPath("$.studentId") { value(studentId) }
                jsonPath("$.classroomId") { value(classroomId) }
                jsonPath("$.id") { exists() }
            }
    }

    @Test
    fun `lists students enrolled in classroom`() {
        val studentId = createStudent()
        val classroomId = createClassroom()

        mockMvc.post(
            "/api/classrooms/$classroomId/students/$studentId"
        )
            .andExpect {
                status { isCreated() }
            }

        mockMvc.get(
            "/api/classrooms/$classroomId/students"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$[0].studentId") { value(studentId) }
                jsonPath("$[0].classroomId") { value(classroomId) }
            }
    }

    @Test
    fun `removes student from classroom`() {
        val studentId = createStudent()
        val classroomId = createClassroom()

        mockMvc.post(
            "/api/classrooms/$classroomId/students/$studentId"
        )
            .andExpect {
                status { isCreated() }
            }

        mockMvc.delete(
            "/api/classrooms/$classroomId/students/$studentId"
        )
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    fun `rejects duplicate enrollment`() {
        val studentId = createStudent()
        val classroomId = createClassroom()

        mockMvc.post(
            "/api/classrooms/$classroomId/students/$studentId"
        )
            .andExpect {
                status { isCreated() }
            }

        mockMvc.post(
            "/api/classrooms/$classroomId/students/$studentId"
        )
            .andExpect {
                status { isConflict() }
            }
    }

    @Test
    fun `returns 404 when student does not exist`() {
        val classroomId = createClassroom()

        mockMvc.post(
            "/api/classrooms/$classroomId/students/999999"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns 404 when classroom does not exist`() {
        val studentId = createStudent()

        mockMvc.post(
            "/api/classrooms/999999/students/$studentId"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns 404 when enrollment does not exist`() {
        val studentId = createStudent()
        val classroomId = createClassroom()

        mockMvc.delete(
            "/api/classrooms/$classroomId/students/$studentId"
        )
            .andExpect {
                status { isNotFound() }
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

        return Regex(""""id":(\d+)""")
            .find(result.response.contentAsString)
            ?.groupValues
            ?.get(1)
            ?.toLong()
            ?: error("Student ID was not returned")
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

        return Regex(""""id":(\d+)""")
            .find(result.response.contentAsString)
            ?.groupValues
            ?.get(1)
            ?.toLong()
            ?: error("Classroom ID was not returned")
    }

    @Test
    fun `creates audit log when student is enrolled`() {
        val studentId = createStudent()
        val classroomId = createClassroom()

        val result = mockMvc.post(
            "/api/classrooms/$classroomId/students/$studentId"
        )
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        val enrollmentId =
            extractId(result.response.contentAsString)

        val auditLog = auditLogRepository.findAll()
            .filter {
                it.resourceType ==
                        AuditResourceType.STUDENT_ENROLLMENT &&
                        it.resourceId == enrollmentId
            }
            .last { it.action == AuditAction.ASSIGN }

        assertEquals(AuditAction.ASSIGN, auditLog.action)
        assertEquals(
            AuditResourceType.STUDENT_ENROLLMENT,
            auditLog.resourceType
        )
        assertEquals(enrollmentId, auditLog.resourceId)
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
    fun `creates audit log when student enrollment is removed`() {
        val studentId = createStudent()
        val classroomId = createClassroom()

        val result = mockMvc.post(
            "/api/classrooms/$classroomId/students/$studentId"
        )
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        val enrollmentId =
            extractId(result.response.contentAsString)

        mockMvc.delete(
            "/api/classrooms/$classroomId/students/$studentId"
        )
            .andExpect {
                status { isNoContent() }
            }

        val auditLog = auditLogRepository.findAll()
            .filter {
                it.resourceType ==
                        AuditResourceType.STUDENT_ENROLLMENT &&
                        it.resourceId == enrollmentId
            }
            .last { it.action == AuditAction.REMOVE }

        assertEquals(AuditAction.REMOVE, auditLog.action)
        assertEquals(
            AuditResourceType.STUDENT_ENROLLMENT,
            auditLog.resourceType
        )
        assertEquals(enrollmentId, auditLog.resourceId)
        assertEquals(
            "test-admin-subject",
            auditLog.actorSubject
        )
        assertEquals(
            "test-admin",
            auditLog.actorUsername
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