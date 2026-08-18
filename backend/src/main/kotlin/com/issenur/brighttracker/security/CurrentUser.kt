package com.issenur.brighttracker.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class CurrentUser {

    fun subject(): String {
        val authentication = SecurityContextHolder
            .getContext()
            .authentication as JwtAuthenticationToken

        return authentication.token.subject
            ?: error("Authenticated user is missing subject claim")
    }

    fun username(): String {
        val authentication = SecurityContextHolder
            .getContext()
            .authentication as JwtAuthenticationToken

        return authentication.token
            .getClaimAsString("preferred_username")
            ?: error("Authenticated user is missing preferred_username claim")
    }
}