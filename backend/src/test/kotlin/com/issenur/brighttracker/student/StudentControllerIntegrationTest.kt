package com.issenur.brighttracker.student

import org.junit.jupiter.api.Test
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.put
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class StudentControllerIntegrationTest {

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
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @Test
    fun `creates a student`() {
        mockMvc.post("/api/students") {
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
                jsonPath("$.firstName") { value("Amina") }
                jsonPath("$.lastName") { value("Ahmed") }
                jsonPath("$.id") { exists() }
            }
    }

    @Test
    fun `gets all students`() {
        mockMvc.get("/api/students")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `returns 404 when student does not exist`() {
        mockMvc.get("/api/students/999")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.message") {
                    value("Student with id 999 was not found")
                }
            }
    }

    @Test
    fun `rejects invalid student request`() {
        mockMvc.post("/api/students") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "firstName": "",
              "lastName": "",
              "dateOfBirth": "2018-05-14",
              "gradeLevel": "",
              "status": "ACTIVE"
            }
        """.trimIndent()
        }
            .andExpect {
                status { isBadRequest() }
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

    @Test
    fun `gets student by id`() {
        val id = createStudent()

        mockMvc.get("/api/students/$id")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(id) }
                jsonPath("$.firstName") { value("Amina") }
            }
    }

    @Test
    fun `updates a student`() {
        val id = createStudent()

        mockMvc.put("/api/students/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "firstName": "Amina",
              "lastName": "Ahmed",
              "dateOfBirth": "2018-05-14",
              "gradeLevel": "2",
              "status": "ACTIVE"
            }
        """.trimIndent()
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.gradeLevel") { value("2") }
            }
    }

    @Test
    fun `deletes a student`() {
        val id = createStudent()

        mockMvc.delete("/api/students/$id")
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("/api/students/$id")
            .andExpect {
                status { isNotFound() }
            }
    }
}