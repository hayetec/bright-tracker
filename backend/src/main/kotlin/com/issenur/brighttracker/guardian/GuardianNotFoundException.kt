package com.issenur.brighttracker.guardian

class GuardianNotFoundException(
    id: Long
) : RuntimeException(
    "Guardian with id $id was not found"
)