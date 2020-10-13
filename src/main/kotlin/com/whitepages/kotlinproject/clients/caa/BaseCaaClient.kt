package com.whitepages.kotlinproject.clients.caa

import com.whitepages.kotlinproject.AppProperties
import com.whitepages.kotlinproject.filter.ProjectException
import com.whitepages.kotlinproject.presenters.consumerApps.BaseCaaResponse
import com.whitepages.kotlinproject.protocols.HttpInterface
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

/**
 * Consumer Apps Api returns a 200 to signal it received a message, and in its body there is normally
 * a status int, a results object or an errors object. Here we check the status, if its not a 2XX we
 * throw an exception which allows error handling to be done in the right place.
 */
@Component
class BaseCaaClient(appProperties: AppProperties) {
    val consumerAppsUrl: String = appProperties.consumerAppsUrl

    @Autowired
    lateinit var http: HttpInterface

    fun <From : BaseCaaResponse, To> get(subClient: String, path: String, params: Map<String, Any>, shouldEncode: Boolean = true, responseType: ParameterizedTypeReference<From>): To {
        val response = http.get(
                client = "$CONSUMER_APPS_CLIENT - $subClient",
                url = "$consumerAppsUrl/$path",
                params = params,
                headers = HttpHeaders().apply {},
                shouldEncode = shouldEncode,
                returnType = responseType)
        return interpretResponse(response.body!!)
    }

    fun <From : BaseCaaResponse, To> put(subClient: String, path: String, body: List<Pair<String, Any?>>, responseType: ParameterizedTypeReference<From>): To {
        val encoded = body.filter { it.second != null }.joinToString(separator = "&") { "${it.first}=${it.second}" }
        val response = http.put(
                client = "$CONSUMER_APPS_CLIENT - $subClient",
                url = "$consumerAppsUrl/$path",
                body = encoded,
                headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED },
                returnType = responseType)
        return interpretResponse(response.body!!)
    }

    fun <From : BaseCaaResponse, To> post(subClient: String, path: String, body: List<Pair<String, Any?>>, responseType: ParameterizedTypeReference<From>): To {
        val encoded = body.filter { it.second != null }.joinToString(separator = "&") { "${it.first}=${it.second}" }
        val response = http.post(
                client = "$CONSUMER_APPS_CLIENT - $subClient",
                url = "$consumerAppsUrl/$path",
                body = encoded,
                headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED },
                returnType = responseType)
        return interpretResponse(response.body!!)
    }

    fun <From : BaseCaaResponse, To> post(subClient: String, path: String, body: String, responseType: ParameterizedTypeReference<From>): To {
        val response = http.post(
                client = "$CONSUMER_APPS_CLIENT - $subClient",
                url = "$consumerAppsUrl/$path",
                body = body,
                headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED },
                returnType = responseType)
        return interpretResponse(response.body!!)
    }

    private fun <From : BaseCaaResponse, To> interpretResponse(from: From): To {
        try {
            val goodResponse = from.statusCode in 200..299
            if (!goodResponse) {
                val errors = from.errors
                throw ProjectException(HttpStatus.valueOf(from.statusCode), errors)
            }

            @Suppress("UNCHECKED_CAST")
            return from.result as To
        } catch (e: NullPointerException) {
            throw ProjectException(HttpStatus.BAD_REQUEST, "Something went wrong converting the response")
        }
    }

    // This proved pretty helpful during development, it just makes the get call, no frills
    // wanted to keep this here in case someone was having a hard time.
    fun getDebug(subclient: String, path: String, params: Map<String, Any>) = http.get<String>(
                client = "$CONSUMER_APPS_CLIENT - $subclient",
                url = "$consumerAppsUrl/$path",
                params = params,
                headers = HttpHeaders().apply {},
                returnType = ParameterizedTypeReference.forType(String::class.java))

    // use at your own risk! This is hard to test (but it works)
//    final inline fun <reified T: Any> typeRef(): ParameterizedTypeReference<T> = object: ParameterizedTypeReference<T>(){}

    companion object {
        const val CONSUMER_APPS_CLIENT = "consumer apps base client" // name in metrics and logs
    }
}
