package com.whitepages.kotlinproject.protocols.logging

import java.util.UUID

interface UuidGeneratorInterface {
    fun generateRandomUuid(): String
}

class UuidGenerator : UuidGeneratorInterface {
    override fun generateRandomUuid(): String {
        return UUID.randomUUID().toString()
    }
}
