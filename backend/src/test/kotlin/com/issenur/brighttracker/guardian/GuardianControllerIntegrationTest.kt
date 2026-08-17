package com.issenur.brighttracker.guardian

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class GuardianControllerIntegrationTest {

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
    fun `creates a guardian`() {
        mockMvc.post("/api/guardians") {
            contentType = MediaType.APPLICATION_JSON
            content = guardianRequest(
                firstName = "Fatima",
                lastName = "Ahmed",
                phoneNumber = "6125551234",
                email = "fatima@example.com"
            )
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.id") { exists() }
                jsonPath("$.firstName") { value("Fatima") }
                jsonPath("$.lastName") { value("Ahmed") }
                jsonPath("$.phoneNumber") { value("6125551234") }
                jsonPath("$.email") { value("fatima@example.com") }
            }
    }

    @Test
    fun `gets all guardians`() {
        createGuardian()

        mockMvc.get("/api/guardians")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].id") { exists() }
            }
    }

    @Test
    fun `gets guardian by id`() {
        val guardianId = createGuardian()

        mockMvc.get("/api/guardians/$guardianId")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(guardianId) }
                jsonPath("$.firstName") { value("Fatima") }
                jsonPath("$.lastName") { value("Ahmed") }
            }
    }

    @Test
    fun `updates a guardian`() {
        val guardianId = createGuardian()

        mockMvc.put("/api/guardians/$guardianId") {
            contentType = MediaType.APPLICATION_JSON
            content = guardianRequest(
                firstName = "Fatima",
                lastName = "Ali",
                phoneNumber = "6125559999",
                email = "fatima.ali@example.com"
            )
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(guardianId) }
                jsonPath("$.lastName") { value("Ali") }
                jsonPath("$.phoneNumber") { value("6125559999") }
                jsonPath("$.email") {
                    value("fatima.ali@example.com")
                }
            }
    }

    @Test
    fun `deletes a guardian`() {
        val guardianId = createGuardian()

        mockMvc.delete("/api/guardians/$guardianId")
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("/api/guardians/$guardianId")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns 404 when guardian does not exist`() {
        mockMvc.get("/api/guardians/999999")
            .andExpect {
                status { isNotFound() }
            }
    }

    private fun createGuardian(): Long {
        val result = mockMvc.post("/api/guardians") {
            contentType = MediaType.APPLICATION_JSON
            content = guardianRequest(
                firstName = "Fatima",
                lastName = "Ahmed",
                phoneNumber = "6125551234",
                email = "fatima-${System.nanoTime()}@example.com"
            )
        }
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        return extractId(result.response.contentAsString)
    }

    private fun guardianRequest(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        email: String?
    ): String =
        """
            {
              "firstName": "$firstName",
              "lastName": "$lastName",
              "phoneNumber": "$phoneNumber",
              "email": "$email"
            }
        """.trimIndent()

    private fun extractId(response: String): Long =
        Regex(""""id":(\d+)""")
            .find(response)
            ?.groupValues
            ?.get(1)
            ?.toLong()
            ?: error("Guardian ID was not returned")
}