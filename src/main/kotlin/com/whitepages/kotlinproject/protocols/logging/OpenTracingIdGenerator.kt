package com.whitepages.kotlinproject.protocols.logging

import com.whitepages.kotlinproject.protocols.OpenTracingConstants.SPAN_ID_SIZE
import com.whitepages.kotlinproject.protocols.OpenTracingConstants.TRACKING_ID_SIZE
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import kotlin.math.ceil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

interface OpenTracingIdGeneratorInterface {
    fun generateSpanId(): String
    fun generateTrackingId(): String
}

@Component
class OpenTracingIdGenerator : OpenTracingIdGeneratorInterface {
    @Autowired
    private lateinit var uuidGen: UuidGeneratorInterface

    companion object {
        const val UUID_SIZE = 32.0
    }

    override fun generateSpanId(): String = generateHex(SPAN_ID_SIZE)
    override fun generateTrackingId(): String = generateHex(TRACKING_ID_SIZE)

    fun generateHex(size: Int): String {
        if (size <= 0) {
            throw IllegalArgumentException("Can't generate a hex with size less than one.")
        }
        val uuidsToGenerate = ceil(size / UUID_SIZE).toInt()
        val generatedHex = StringBuilder()

        for (i in 0..uuidsToGenerate) {
            val randomHex = uuidGen.generateRandomUuid().replace("-", "")
            generatedHex.append(randomHex)
        }

        return generatedHex.toString().substring(0, size)
    }
}
