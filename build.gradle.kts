import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("org.springframework.boot") version "2.2.0.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
    id("jacoco")
    kotlin("jvm") version "1.3.70"
    kotlin("plugin.spring") version "1.3.70"
}

jacoco {
    toolVersion = "0.8.5"
}

// require test coverage as part of the test task
tasks.test {
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

// html is stored in build/reports/jacoco/test/html
tasks.jacocoTestReport {
    reports {
        xml.isEnabled = false
        csv.isEnabled = false
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            enabled = true
            element = "CLASS"
            includes = listOf("com.whitepages.kotlinproject.*")
            excludes = listOf(
                    "com.whitepages.kotlinproject.protocols.OpenTracingConstants",
                    "com.whitepages.kotlinproject.presenters.consumerApps.CaaProducts",
                    "com.whitepages.kotlinproject.presenters.consumerApps.BaseCaaResponse",
                    "com.whitepages.kotlinproject.presenters.consumerApps.StringCaaResponse",
                    "com.whitepages.kotlinproject.presenters.consumerApps.CaaProduct",
                    "com.whitepages.kotlinproject.filter.FilterOrder",
                    "com.whitepages.kotlinproject.protocols.metrics.WpMonitor",
                    "com.whitepages.kotlinproject.protocols.AccessMessage",
                    "com.whitepages.kotlinproject.filter.LoggingContextFilter",
                    "com.whitepages.kotlinproject.filter.RequestResponseLoggingFilter",
                    "com.whitepages.kotlinproject.protocols.logging.WpLogEventFactory",
                    "com.whitepages.kotlinproject.KotlinProjectKt"
            )

            limit {
                counter = "BRANCH"
                minimum = "0.5".toBigDecimal()
            }

            limit {
                counter = "LINE"
                minimum = "0.4".toBigDecimal()
            }
        }
    }
    dependsOn(tasks.jacocoTestReport)
}

group = "whitepages"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11
val log4j2 = "2.12.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-data-jpa") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-actuator") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
//    still need a sql driver
    implementation("org.postgresql:postgresql")
    compile("io.springfox:springfox-swagger2:2.9.2")
    compile("io.springfox:springfox-swagger-ui:2.9.2")
    implementation("com.segment.analytics.java:analytics:+")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.apache.logging.log4j:log4j-api:$log4j2")
    implementation("org.apache.logging.log4j:log4j-core:$log4j2")
    implementation("org.json:json:20190722")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-core")
    implementation("com.devskiller.friendly-id:friendly-id:1.1.0")
    testImplementation("com.h2database:h2")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0-RC3")
    testImplementation(group = "org.apache.logging.log4j", name = "log4j-core", version = log4j2, classifier = "tests")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
