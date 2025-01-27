package com.breadfinancial.breadpartners.sdk.networking

import com.breadfinancial.breadpartners.sdk.utilities.Logger
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val error: Throwable) : Result<Nothing>()
}

enum class HTTPMethod(val value: String) {
    GET("GET"),
    POST("POST"),
}

interface APIClientProtocol {
    fun request(
        urlString: String,
        method: HTTPMethod,
        body: Any?,
        completion: (Result<Any>) -> Unit
    )
}

class APIClient(
    private val coroutineScope: CoroutineScope,
    private val logger: Logger
) : APIClientProtocol {

    override fun request(
        urlString: String,
        method: HTTPMethod,
        body: Any?,
        completion: (Result<Any>) -> Unit
    ) {
        coroutineScope.launch {
            try {
                // Create URL and open connection
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = method.value
                connection.setRequestProperty("Content-Type", "application/json")

                val headers = mapOf("content-type" to "application/json")
                headers.forEach { (key, value) ->
                    connection.setRequestProperty(key, value)
                }

                logger.logRequestDetails(
                    urlString,
                    method.value,
                    headers,
                    body?.let { Gson().toJson(it).toByteArray() })

                body?.let {
                    connection.doOutput = true
                    val requestBody = Gson().toJson(it)
                    OutputStreamWriter(connection.outputStream).use { writer ->
                        writer.write(requestBody)
                    }
                }

                val responseCode = connection.responseCode
                val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

                logger.logResponseDetails(
                    urlString,
                    responseCode,
                    connection.headerFields,
                    responseMessage.toByteArray()
                )

                // Handle response based on status code
                if (responseCode in 200..299) {
                    val jsonResponse = JSONObject(responseMessage)
                    withContext(Dispatchers.Main) {
                        completion(Result.Success(jsonResponse))
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        completion(Result.Failure(Exception("HTTP Error $responseCode: $responseMessage")))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    completion(Result.Failure(e))
                }
            }
        }
    }
}