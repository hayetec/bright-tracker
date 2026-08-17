package com.issenur.brighttracker.guardian

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
import org.springframework.test.web.servlet.put
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class StudentGuardianControllerIntegrationTest {

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
    fun `links guardian to student`() {
        val studentId = createStudent()
        val guardianId = createGuardian()

        mockMvc.post(
            "/api/students/$studentId/guardians/$guardianId"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = relationshipRequest(
                relationship = "MOTHER",
                isPrimaryContact = true,
                isEmergencyContact = true
            )
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.id") { exists() }
                jsonPath("$.studentId") { value(studentId) }
                jsonPath("$.guardianId") { value(guardianId) }
                jsonPath("$.relationship") { value("MOTHER") }
                jsonPath("$.isPrimaryContact") { value(true) }
                jsonPath("$.isEmergencyContact") { value(true) }
            }
    }

    @Test
    fun `gets guardians by student`() {
        val studentId = createStudent()
        val guardianId = createGuardian()

        linkGuardian(studentId, guardianId)

        mockMvc.get(
            "/api/students/$studentId/guardians"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$[0].studentId") { value(studentId) }
                jsonPath("$[0].guardianId") { value(guardianId) }
            }
    }

    @Test
    fun `gets students by guardian`() {
        val studentId = createStudent()
        val guardianId = createGuardian()

        linkGuardian(studentId, guardianId)

        mockMvc.get(
            "/api/guardians/$guardianId/students"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$[0].studentId") { value(studentId) }
                jsonPath("$[0].guardianId") { value(guardianId) }
            }
    }

    @Test
    fun `updates student guardian relationship`() {
        val studentId = createStudent()
        val guardianId = createGuardian()

        linkGuardian(studentId, guardianId)

        mockMvc.put(
            "/api/students/$studentId/guardians/$guardianId"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = relationshipRequest(
                relationship = "FATHER",
                isPrimaryContact = false,
                isEmergencyContact = true
            )
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.relationship") { value("FATHER") }
                jsonPath("$.isPrimaryContact") { value(false) }
                jsonPath("$.isEmergencyContact") { value(true) }
            }
    }

    @Test
    fun `removes student guardian relationship`() {
        val studentId = createStudent()
        val guardianId = createGuardian()

        linkGuardian(studentId, guardianId)

        mockMvc.delete(
            "/api/students/$studentId/guardians/$guardianId"
        )
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.delete(
            "/api/students/$studentId/guardians/$guardianId"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `rejects duplicate student guardian relationship`() {
        val studentId = createStudent()
        val guardianId = createGuardian()

        linkGuardian(studentId, guardianId)

        mockMvc.post(
            "/api/students/$studentId/guardians/$guardianId"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = relationshipRequest("MOTHER")
        }
            .andExpect {
                status { isConflict() }
            }
    }

    @Test
    fun `returns 404 when student does not exist`() {
        val guardianId = createGuardian()

        mockMvc.post(
            "/api/students/999999/guardians/$guardianId"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = relationshipRequest("MOTHER")
        }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns 404 when guardian does not exist`() {
        val studentId = createStudent()

        mockMvc.post(
            "/api/students/$studentId/guardians/999999"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = relationshipRequest("MOTHER")
        }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns 404 when relationship does not exist`() {
        val studentId = createStudent()
        val guardianId = createGuardian()

        mockMvc.delete(
            "/api/students/$studentId/guardians/$guardianId"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    private fun linkGuardian(
        studentId: Long,
        guardianId: Long
    ) {
        mockMvc.post(
            "/api/students/$studentId/guardians/$guardianId"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = relationshipRequest("MOTHER")
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

        return extractId(result.response.contentAsString)
    }

    private fun createGuardian(): Long {
        val result = mockMvc.post("/api/guardians") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "firstName": "Fatima",
                  "lastName": "Ahmed",
                  "phoneNumber": "6125551234",
                  "email": "guardian-${System.nanoTime()}@example.com"
                }
            """.trimIndent()
        }
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        return extractId(result.response.contentAsString)
    }

    private fun relationshipRequest(
        relationship: String,
        isPrimaryContact: Boolean = false,
        isEmergencyContact: Boolean = false
    ): String =
        """
            {
              "relationship": "$relationship",
              "isPrimaryContact": $isPrimaryContact,
              "isEmergencyContact": $isEmergencyContact
            }
        """.trimIndent()

    private fun extractId(response: String): Long =
        Regex(""""id":(\d+)""")
            .find(response)
            ?.groupValues
            ?.get(1)
            ?.toLong()
            ?: error("ID was not returned")
}