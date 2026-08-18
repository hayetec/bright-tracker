package com.issenur.brighttracker.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class KeycloakJwtRolesConverter : Converter<Jwt, Collection<GrantedAuthority>> {

    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val resourceAccess = jwt.getClaim<Map<String, Any>>("resource_access")
            ?: return emptyList()

        val clientAccess = resourceAccess["bright-tracker-api"] as? Map<*, *>
            ?: return emptyList()

        val roles = clientAccess["roles"] as? Collection<*>
            ?: return emptyList()

        return roles
            .filterIsInstance<String>()
            .map { role -> SimpleGrantedAuthority("ROLE_$role") }
    }
}