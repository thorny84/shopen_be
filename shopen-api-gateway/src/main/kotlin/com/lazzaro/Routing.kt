package com.lazzaro

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

val log: Logger = LoggerFactory.getLogger("APIGateway")

fun Application.configureRouting() {
    val client = HttpClient()

    install(RateLimit) {
        global {
            rateLimiter(limit = 5, refillPeriod = 60.seconds)
        }
    }
    routing {
        post("/user") {
            try {
                com.lazzaro.log.info("get request: ${call.request.uri}")
                val backendResponse: HttpResponse = withContext(Dispatchers.IO) {
                    client.post("http://auth:8080/user") {
                        call.request.headers.forEach { header, values ->
                            headers.appendAll(header, values)
                        }
                        contentType(ContentType.Application.Json)
                        val body = call.receiveText()
                        setBody(body)
                    }
                }
                val body = backendResponse.bodyAsText()
                com.lazzaro.log.info("auth response service: ${backendResponse.status}")
                call.respond(backendResponse.status, body)
            } catch (e: Exception) {
                com.lazzaro.log.error("Error communicating with auth service: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, "Internal server error")
            }
        }
    }
}
