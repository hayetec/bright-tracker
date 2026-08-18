package com.issenur.brighttracker.assignment

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class StaffAssignmentControllerIntegrationTest {

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
                get("/")
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
    fun `assigns teacher to classroom`() {
        val classroomId = createClassroom()
        val staffId = createStaff("TEACHER")

        mockMvc.post(
            "/api/classrooms/$classroomId/staff/$staffId"
        )
            .andExpect {
                status { isCreated() }
                jsonPath("$.id") { exists() }
                jsonPath("$.classroomId") { value(classroomId) }
                jsonPath("$.staffId") { value(staffId) }
            }
    }

    @Test
    fun `lists staff assigned to classroom`() {
        val classroomId = createClassroom()
        val staffId = createStaff("TEACHER")

        mockMvc.post(
            "/api/classrooms/$classroomId/staff/$staffId"
        )
            .andExpect {
                status { isCreated() }
            }

        mockMvc.get(
            "/api/classrooms/$classroomId/staff"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$[0].classroomId") {
                    value(classroomId)
                }
                jsonPath("$[0].staffId") {
                    value(staffId)
                }
            }
    }

    @Test
    fun `removes staff assignment`() {
        val classroomId = createClassroom()
        val staffId = createStaff("TEACHER")

        mockMvc.post(
            "/api/classrooms/$classroomId/staff/$staffId"
        )
            .andExpect {
                status { isCreated() }
            }

        mockMvc.delete(
            "/api/classrooms/$classroomId/staff/$staffId"
        )
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    fun `rejects duplicate staff assignment`() {
        val classroomId = createClassroom()
        val staffId = createStaff("TEACHER")

        mockMvc.post(
            "/api/classrooms/$classroomId/staff/$staffId"
        )
            .andExpect {
                status { isCreated() }
            }

        mockMvc.post(
            "/api/classrooms/$classroomId/staff/$staffId"
        )
            .andExpect {
                status { isConflict() }
            }
    }

    @Test
    fun `returns 404 when classroom does not exist`() {
        val staffId = createStaff("TEACHER")

        mockMvc.post(
            "/api/classrooms/999999/staff/$staffId"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns 404 when staff does not exist`() {
        val classroomId = createClassroom()

        mockMvc.post(
            "/api/classrooms/$classroomId/staff/999999"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns 404 when assignment does not exist`() {
        val classroomId = createClassroom()
        val staffId = createStaff("TEACHER")

        mockMvc.delete(
            "/api/classrooms/$classroomId/staff/$staffId"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `rejects staff role that cannot be assigned to classroom`() {
        val classroomId = createClassroom()
        val staffId = createStaff("ADMIN")

        mockMvc.post(
            "/api/classrooms/$classroomId/staff/$staffId"
        )
            .andExpect {
                status { isBadRequest() }
            }
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

        return extractId(result.response.contentAsString)
    }

    private fun createStaff(role: String): Long {
        val email =
            "staff-${System.nanoTime()}@example.com"

        val result = mockMvc.post("/api/staff") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "firstName": "Hassan",
                  "lastName": "Ali",
                  "email": "$email",
                  "phoneNumber": "6125551234",
                  "role": "$role",
                  "status": "ACTIVE"
                }
            """.trimIndent()
        }
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        return extractId(result.response.contentAsString)
    }

    @Test
    fun `creates audit log when staff is assigned to classroom`() {
        val classroomId = createClassroom()
        val staffId = createStaff("TEACHER")

        val result = mockMvc.post(
            "/api/classrooms/$classroomId/staff/$staffId"
        )
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        val assignmentId =
            extractId(result.response.contentAsString)

        val auditLog = auditLogRepository.findAll()
            .filter {
                it.resourceType ==
                        AuditResourceType.STAFF_ASSIGNMENT &&
                        it.resourceId == assignmentId
            }
            .last { it.action == AuditAction.ASSIGN }

        assertEquals(AuditAction.ASSIGN, auditLog.action)
        assertEquals(
            AuditResourceType.STAFF_ASSIGNMENT,
            auditLog.resourceType
        )
        assertEquals(assignmentId, auditLog.resourceId)
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
    fun `creates audit log when staff assignment is removed`() {
        val classroomId = createClassroom()
        val staffId = createStaff("TEACHER")

        val result = mockMvc.post(
            "/api/classrooms/$classroomId/staff/$staffId"
        )
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        val assignmentId =
            extractId(result.response.contentAsString)

        mockMvc.delete(
            "/api/classrooms/$classroomId/staff/$staffId"
        )
            .andExpect {
                status { isNoContent() }
            }

        val auditLog = auditLogRepository.findAll()
            .filter {
                it.resourceType ==
                        AuditResourceType.STAFF_ASSIGNMENT &&
                        it.resourceId == assignmentId
            }
            .last { it.action == AuditAction.REMOVE }

        assertEquals(AuditAction.REMOVE, auditLog.action)
        assertEquals(
            AuditResourceType.STAFF_ASSIGNMENT,
            auditLog.resourceType
        )
        assertEquals(assignmentId, auditLog.resourceId)
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