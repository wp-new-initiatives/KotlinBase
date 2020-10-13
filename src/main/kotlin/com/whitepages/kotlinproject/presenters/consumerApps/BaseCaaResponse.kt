package com.whitepages.kotlinproject.presenters.consumerApps

import com.fasterxml.jackson.annotation.JsonAlias

open class BaseCaaResponse(
    @JsonAlias("status_code")
    open val statusCode: Int,
    open val result: Any?,
    open val errors: Any?
)

class StringCaaResponse(
    @JsonAlias("status_code")
    override val statusCode: Int,
    val status: String,
    override val result: String?,
    override val errors: Map<String, Any>?
) : BaseCaaResponse(statusCode, result, errors)
