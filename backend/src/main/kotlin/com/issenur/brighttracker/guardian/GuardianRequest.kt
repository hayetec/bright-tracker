package com.issenur.brighttracker.guardian

data class GuardianRequest(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String? = null
)