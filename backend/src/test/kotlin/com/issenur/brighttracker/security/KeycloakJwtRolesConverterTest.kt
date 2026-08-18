package com.issenur.brighttracker.security

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant

class KeycloakJwtRolesConverterTest {

    private val converter = KeycloakJwtRolesConverter()

    @Test
    fun `converts Keycloak client roles to Spring authorities`() {
        val jwt = Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(300),
            mapOf("alg" to "RS256"),
            mapOf(
                "resource_access" to mapOf(
                    "bright-tracker-api" to mapOf(
                        "roles" to listOf("ADMIN", "STAFF")
                    )
                )
            )
        )

        val authorities = converter.convert(jwt)
            .map { it.authority }

        assertEquals(
            listOf("ROLE_ADMIN", "ROLE_STAFF"),
            authorities
        )
    }
}