package com.issenur.brighttracker.assignment

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ClassroomStaffAssignmentControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

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

    private fun extractId(response: String): Long =
        Regex(""""id":(\d+)""")
            .find(response)
            ?.groupValues
            ?.get(1)
            ?.toLong()
            ?: error("ID was not returned")
}