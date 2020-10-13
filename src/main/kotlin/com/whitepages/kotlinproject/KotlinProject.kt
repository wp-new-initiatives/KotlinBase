package com.whitepages.kotlinproject

import java.net.InetAddress
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class KotlinProject

fun main(args: Array<String>) {
    System.setProperty("HOSTNAME", InetAddress.getLocalHost().hostName)

    runApplication<KotlinProject>(*args)
}
