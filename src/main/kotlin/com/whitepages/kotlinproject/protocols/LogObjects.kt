package com.whitepages.kotlinproject.protocols

data class AccessMessage(
    val duration: Long,
    val method: String,
    val uri: String,
    val status: Int
)

data class ClientMessage(
    val client: String,
    val duration: Long,
    val out: String,
    val status: Int,
    val _in: String
)
