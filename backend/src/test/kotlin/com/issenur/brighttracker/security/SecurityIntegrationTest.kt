package com.issenur.brighttracker.security

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class SecurityIntegrationTest {

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
    fun `rejects unauthenticated request`() {
        mockMvc.get("/api/students")
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    fun `allows staff to read`() {
        mockMvc.get("/api/students") {
            with(
                jwt().authorities(
                    SimpleGrantedAuthority("ROLE_STAFF")
                )
            )
        }
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `rejects staff write access`() {
        mockMvc.post("/api/students") {
            with(
                jwt().authorities(
                    SimpleGrantedAuthority("ROLE_STAFF")
                )
            )

            contentType = MediaType.APPLICATION_JSON
            content = validStudentRequest()
        }
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `allows admin to read`() {
        mockMvc.get("/api/students") {
            with(
                jwt().authorities(
                    SimpleGrantedAuthority("ROLE_ADMIN")
                )
            )
        }
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `allows admin to write`() {
        mockMvc.post("/api/students") {
            with(
                jwt().authorities(
                    SimpleGrantedAuthority("ROLE_ADMIN")
                )
            )

            contentType = MediaType.APPLICATION_JSON
            content = validStudentRequest()
        }
            .andExpect {
                status { isCreated() }
            }
    }

    private fun validStudentRequest(): String =
        """
            {
              "firstName": "Security",
              "lastName": "Test",
              "dateOfBirth": "2018-01-01",
              "gradeLevel": "1",
              "status": "ACTIVE"
            }
        """.trimIndent()
}