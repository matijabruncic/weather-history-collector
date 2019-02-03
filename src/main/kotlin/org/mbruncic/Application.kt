package org.mbruncic

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

val log = KotlinLogging.logger { }

@SpringBootApplication
@EnableScheduling
@EntityScan("org.mbruncic")
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
