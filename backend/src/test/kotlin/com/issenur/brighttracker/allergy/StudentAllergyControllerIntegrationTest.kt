package com.issenur.brighttracker.allergy

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
class StudentAllergyControllerIntegrationTest {

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
    fun `creates student allergy`() {
        val studentId = createStudent()

        mockMvc.post("/api/students/$studentId/allergies") {
            contentType = MediaType.APPLICATION_JSON
            content = createRequest(
                allergen = "Peanuts",
                severity = "SEVERE",
                notes = "Carries epinephrine"
            )
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.id") { exists() }
                jsonPath("$.studentId") { value(studentId) }
                jsonPath("$.allergen") { value("Peanuts") }
                jsonPath("$.severity") { value("SEVERE") }
                jsonPath("$.notes") {
                    value("Carries epinephrine")
                }
            }
    }

    @Test
    fun `gets all allergies for student`() {
        val studentId = createStudent()
        createAllergy(studentId)

        mockMvc.get("/api/students/$studentId/allergies")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].studentId") {
                    value(studentId)
                }
                jsonPath("$[0].allergen") {
                    value("Peanuts")
                }
            }
    }

    @Test
    fun `gets allergy by id`() {
        val studentId = createStudent()
        val allergyId = createAllergy(studentId)

        mockMvc.get(
            "/api/students/$studentId/allergies/$allergyId"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(allergyId) }
                jsonPath("$.studentId") { value(studentId) }
                jsonPath("$.allergen") { value("Peanuts") }
            }
    }

    @Test
    fun `updates student allergy`() {
        val studentId = createStudent()
        val allergyId = createAllergy(studentId)

        mockMvc.put(
            "/api/students/$studentId/allergies/$allergyId"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = updateRequest(
                allergen = "Tree Nuts",
                severity = "MODERATE",
                notes = "Avoid mixed nuts"
            )
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.allergen") { value("Tree Nuts") }
                jsonPath("$.severity") { value("MODERATE") }
                jsonPath("$.notes") {
                    value("Avoid mixed nuts")
                }
            }
    }

    @Test
    fun `deletes student allergy`() {
        val studentId = createStudent()
        val allergyId = createAllergy(studentId)

        mockMvc.delete(
            "/api/students/$studentId/allergies/$allergyId"
        )
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get(
            "/api/students/$studentId/allergies/$allergyId"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `rejects duplicate allergen ignoring case`() {
        val studentId = createStudent()

        createAllergy(
            studentId = studentId,
            allergen = "Peanuts"
        )

        mockMvc.post("/api/students/$studentId/allergies") {
            contentType = MediaType.APPLICATION_JSON
            content = createRequest(
                allergen = "peanuts",
                severity = "SEVERE"
            )
        }
            .andExpect {
                status { isConflict() }
            }
    }

    @Test
    fun `trims allergen before saving`() {
        val studentId = createStudent()

        mockMvc.post("/api/students/$studentId/allergies") {
            contentType = MediaType.APPLICATION_JSON
            content = createRequest(
                allergen = "  Peanuts  ",
                severity = "SEVERE"
            )
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.allergen") {
                    value("Peanuts")
                }
            }
    }

    @Test
    fun `rejects update when allergen duplicates another allergy`() {
        val studentId = createStudent()

        createAllergy(
            studentId = studentId,
            allergen = "Peanuts"
        )

        val secondAllergyId = createAllergy(
            studentId = studentId,
            allergen = "Milk"
        )

        mockMvc.put(
            "/api/students/$studentId/allergies/$secondAllergyId"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = updateRequest(
                allergen = "PEANUTS",
                severity = "MODERATE"
            )
        }
            .andExpect {
                status { isConflict() }
            }
    }

    @Test
    fun `allows update when allergen name differs only by case from itself`() {
        val studentId = createStudent()

        val allergyId = createAllergy(
            studentId = studentId,
            allergen = "Peanuts"
        )

        mockMvc.put(
            "/api/students/$studentId/allergies/$allergyId"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = updateRequest(
                allergen = "PEANUTS",
                severity = "MILD"
            )
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.allergen") {
                    value("PEANUTS")
                }
                jsonPath("$.severity") {
                    value("MILD")
                }
            }
    }

    @Test
    fun `returns 404 when student does not exist`() {
        mockMvc.post("/api/students/999999/allergies") {
            contentType = MediaType.APPLICATION_JSON
            content = createRequest(
                allergen = "Peanuts",
                severity = "SEVERE"
            )
        }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns 404 when allergy does not exist`() {
        val studentId = createStudent()

        mockMvc.get(
            "/api/students/$studentId/allergies/999999"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    private fun createAllergy(
        studentId: Long,
        allergen: String = "Peanuts"
    ): Long {
        val result = mockMvc.post(
            "/api/students/$studentId/allergies"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = createRequest(
                allergen = allergen,
                severity = "SEVERE"
            )
        }
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        return extractId(result.response.contentAsString)
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

    private fun createRequest(
        allergen: String,
        severity: String,
        notes: String? = null
    ): String =
        """
            {
              "allergen": "$allergen",
              "severity": "$severity",
              "notes": ${jsonString(notes)}
            }
        """.trimIndent()

    private fun updateRequest(
        allergen: String,
        severity: String,
        notes: String? = null
    ): String =
        """
            {
              "allergen": "$allergen",
              "severity": "$severity",
              "notes": ${jsonString(notes)}
            }
        """.trimIndent()

    private fun jsonString(value: String?): String =
        value?.let { "\"$it\"" } ?: "null"

    private fun extractId(response: String): Long =
        Regex(""""id":(\d+)""")
            .find(response)
            ?.groupValues
            ?.get(1)
            ?.toLong()
            ?: error("ID was not returned")

    @Test
    fun `creates audit log when student allergy is created`() {
        val studentId = createStudent()

        val result = mockMvc.post("/api/students/$studentId/allergies") {
            contentType = MediaType.APPLICATION_JSON
            content = createRequest(
                allergen = "Audit Peanuts",
                severity = "SEVERE",
                notes = "Audit test"
            )
        }
            .andExpect {
                status { isCreated() }
            }
            .andReturn()

        val allergyId = extractId(result.response.contentAsString)

        val auditLog = auditLogRepository.findAll()
            .filter {
                it.resourceType == AuditResourceType.STUDENT_ALLERGY &&
                        it.resourceId == allergyId
            }
            .last { it.action == AuditAction.CREATE }

        assertEquals(AuditAction.CREATE, auditLog.action)
        assertEquals(AuditResourceType.STUDENT_ALLERGY, auditLog.resourceType)
        assertEquals(allergyId, auditLog.resourceId)
        assertEquals("test-admin-subject", auditLog.actorSubject)
        assertEquals("test-admin", auditLog.actorUsername)
    }

    @Test
    fun `creates audit log when student allergy is updated`() {
        val studentId = createStudent()
        val allergyId = createAllergy(studentId)

        mockMvc.put(
            "/api/students/$studentId/allergies/$allergyId"
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = updateRequest(
                allergen = "Tree Nuts",
                severity = "MODERATE",
                notes = "Updated audit test"
            )
        }
            .andExpect {
                status { isOk() }
            }

        val auditLog = auditLogRepository.findAll()
            .filter {
                it.resourceType == AuditResourceType.STUDENT_ALLERGY &&
                        it.resourceId == allergyId
            }
            .last { it.action == AuditAction.UPDATE }

        assertEquals(AuditAction.UPDATE, auditLog.action)
        assertEquals(AuditResourceType.STUDENT_ALLERGY, auditLog.resourceType)
        assertEquals(allergyId, auditLog.resourceId)
        assertEquals("test-admin-subject", auditLog.actorSubject)
        assertEquals("test-admin", auditLog.actorUsername)
    }

    @Test
    fun `creates audit log when student allergy is deleted`() {
        val studentId = createStudent()
        val allergyId = createAllergy(studentId)

        mockMvc.delete(
            "/api/students/$studentId/allergies/$allergyId"
        )
            .andExpect {
                status { isNoContent() }
            }

        val auditLog = auditLogRepository.findAll()
            .filter {
                it.resourceType == AuditResourceType.STUDENT_ALLERGY &&
                        it.resourceId == allergyId
            }
            .last { it.action == AuditAction.DELETE }

        assertEquals(AuditAction.DELETE, auditLog.action)
        assertEquals(AuditResourceType.STUDENT_ALLERGY, auditLog.resourceType)
        assertEquals(allergyId, auditLog.resourceId)
        assertEquals("test-admin-subject", auditLog.actorSubject)
        assertEquals("test-admin", auditLog.actorUsername)
    }
}