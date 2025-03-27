//------------------------------------------------------------------------------
//  File:          APIClient.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.networking

import com.breadfinancial.breadpartners.sdk.utilities.CommonUtils
import com.breadfinancial.breadpartners.sdk.utilities.Constants
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
    GET("GET"), POST("POST"),OPTIONS("OPTIONS")
}

/**
 * A utility class for making HTTP API requests.
 */
class APIClient(
    private val coroutineScope: CoroutineScope,
    private val logger: Logger,
    private val commonUtils: CommonUtils
) {

    /**
     * Generic API call function.
     *
     * - Parameters:
     *   - urlString: The URL endpoint as a string.
     *   - method: HTTP method (e.g., "GET", "POST").
     *   - body: Optional request body, can be a map (`Map<String, Any>`) or a model that implements `Serializable`.
     *   - headers: Optional headers body, can be a map (`Map<String, Any>`) or null.
     *   - completion: A lambda to handle the result, returning success with response or failure with error.
     */
    fun request(
        urlString: String,
        method: HTTPMethod,
        body: Any?,
        headers: Map<String, String>? = null,
        completion: (Result<Any>) -> Unit
    ) {
        coroutineScope.launch {
            try {
                // Create URL and open connection
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = method.value

                val genericHeader = mapOf(
                    Constants.headerContentType to Constants.headerContentTypeValue,
                    Constants.headerUserAgentKey to commonUtils.getUserAgent(),
                    Constants.headerOriginKey to Constants.headerOriginValue
                )
                val updatedHeaders = (headers ?: emptyMap()) + genericHeader

                updatedHeaders.forEach { (key, value) ->
                    connection.setRequestProperty(key, value)
                }

                logger.logRequestDetails(urlString,
                    method.value,
                    updatedHeaders,
                    body?.let { Gson().toJson(it).toByteArray() })

                body?.let {
                    connection.doOutput = true
                    val requestBody = Gson().toJson(it)
                    val outputStream = connection.outputStream
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(requestBody)
                    }
                }

                val responseCode = connection.responseCode

                val responseMessage = if (responseCode in 200..299) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                }

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