package com.issenur.brighttracker.staff

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
class StaffControllerIntegrationTest {

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
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @Test
    fun `creates a staff member`() {
        val uniqueEmail = "hassan-${System.nanoTime()}@example.com"

        mockMvc.post("/api/staff") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "firstName": "Hassan",
              "lastName": "Ali",
              "email": "$uniqueEmail",
              "phoneNumber": "6125551234",
              "role": "TEACHER",
              "status": "ACTIVE"
            }
        """.trimIndent()
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.firstName") { value("Hassan") }
                jsonPath("$.role") { value("TEACHER") }
                jsonPath("$.id") { exists() }
            }
    }

    @Test
    fun `gets all staff`() {
        mockMvc.get("/api/staff")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `gets staff member by id`() {
        val id = createStaffMember()

        mockMvc.get("/api/staff/$id")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(id) }
                jsonPath("$.firstName") { value("Hassan") }
            }
    }

    @Test
    fun `updates a staff member`() {
        val id = createStaffMember()
        val updatedEmail = "hassan-updated-${System.nanoTime()}@example.com"

        mockMvc.put("/api/staff/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "firstName": "Hassan",
              "lastName": "Ali",
              "email": "$updatedEmail",
              "phoneNumber": "6125551234",
              "role": "TEACHER_AIDE",
              "status": "ACTIVE"
            }
        """.trimIndent()
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.role") { value("TEACHER_AIDE") }
            }
    }

    @Test
    fun `deletes a staff member`() {
        val id = createStaffMember()

        mockMvc.delete("/api/staff/$id")
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("/api/staff/$id")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns 404 when staff member does not exist`() {
        mockMvc.get("/api/staff/999999")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `rejects invalid staff request`() {
        mockMvc.post("/api/staff") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "firstName": "",
              "lastName": "",
              "email": "not-an-email",
              "phoneNumber": "",
              "role": "TEACHER",
              "status": "ACTIVE"
            }
        """.trimIndent()
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(400) }
                jsonPath("$.error") { value("Bad Request") }
                jsonPath("$.message") { value("Validation failed") }
                jsonPath("$.path") { value("/api/staff") }
                jsonPath("$.details.firstName") {
                    value("must not be blank")
                }
                jsonPath("$.details.lastName") {
                    value("must not be blank")
                }
                jsonPath("$.details.email") {
                    value("must be a well-formed email address")
                }
            }
    }
    
    private fun createStaffMember(): Long {
        val result = mockMvc.post("/api/staff") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "firstName": "Hassan",
                  "lastName": "Ali",
                  "email": "hassan@example.com",
                  "phoneNumber": "6125551234",
                  "role": "TEACHER",
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
            ?: error("Staff ID was not returned")
    }
}