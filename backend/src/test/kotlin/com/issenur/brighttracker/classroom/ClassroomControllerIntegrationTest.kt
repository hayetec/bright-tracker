package com.issenur.brighttracker.classroom

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
@WithMockUser(roles = ["ADMIN"])
class ClassroomControllerIntegrationTest {

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUpMockMvc() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .defaultRequest<DefaultMockMvcBuilder>(
                MockMvcRequestBuilders.get("/")
                    .with(
                        jwt().authorities(
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
    fun `creates a classroom`() {
        mockMvc.post("/api/classrooms") {
            contentType = MediaType.APPLICATION_JSON
            content = classroomRequest("Room A", "101")
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.id") { exists() }
                jsonPath("$.name") { value("Room A") }
                jsonPath("$.gradeLevel") { value("1") }
                jsonPath("$.roomNumber") { value("101") }
                jsonPath("$.capacity") { value(20) }
                jsonPath("$.status") { value("ACTIVE") }
            }
    }

    @Test
    fun `gets all classrooms`() {
        createClassroom()

        mockMvc.get("/api/classrooms")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].id") { exists() }
            }
    }

    @Test
    fun `gets classroom by id`() {
        val classroomId = createClassroom()

        mockMvc.get("/api/classrooms/$classroomId")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(classroomId) }
                jsonPath("$.name") { value("Room A") }
            }
    }

    @Test
    fun `updates a classroom`() {
        val classroomId = createClassroom()

        mockMvc.put("/api/classrooms/$classroomId") {
            contentType = MediaType.APPLICATION_JSON
            content = classroomRequest(
                name = "Room B",
                roomNumber = "102",
                capacity = 25
            )
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(classroomId) }
                jsonPath("$.name") { value("Room B") }
                jsonPath("$.roomNumber") { value("102") }
                jsonPath("$.capacity") { value(25) }
            }
    }

    @Test
    fun `deletes a classroom`() {
        val classroomId = createClassroom()

        mockMvc.delete("/api/classrooms/$classroomId")
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("/api/classrooms/$classroomId")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns 404 when classroom does not exist`() {
        mockMvc.get("/api/classrooms/999999")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `rejects invalid classroom request`() {
        mockMvc.post("/api/classrooms") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "name": "",
              "gradeLevel": "",
              "roomNumber": "101",
              "capacity": 0,
              "status": "ACTIVE"
            }
        """.trimIndent()
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(400) }
                jsonPath("$.error") { value("Bad Request") }
                jsonPath("$.message") { value("Validation failed") }
                jsonPath("$.path") { value("/api/classrooms") }
                jsonPath("$.details.name") {
                    value("must not be blank")
                }
                jsonPath("$.details.gradeLevel") {
                    value("must not be blank")
                }
                jsonPath("$.details.capacity") {
                    value("must be greater than or equal to 1")
                }
            }
    }

    private fun createClassroom(): Long {
        val result = mockMvc.post("/api/classrooms") {
            contentType = MediaType.APPLICATION_JSON
            content = classroomRequest("Room A", "101")
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

    private fun classroomRequest(
        name: String,
        roomNumber: String,
        capacity: Int = 20
    ): String =
        """
            {
              "name": "$name",
              "gradeLevel": "1",
              "roomNumber": "$roomNumber",
              "capacity": $capacity,
              "status": "ACTIVE"
            }
        """.trimIndent()
}