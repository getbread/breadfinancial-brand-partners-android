package com.breadfinancial.breadpartners.sdk.utilities

import android.os.Handler
import com.google.gson.Gson

class CommonUtils(
    private val handler: Handler,
    private val alertHandler: AlertHandler
) {

    // Executes a given block after a specified delay.
    //   - delay: The delay in seconds.
    fun executeAfterDelay(delay: Long, completion: () -> Unit) {
        handler.postDelayed({
            completion()
        }, delay)
    }

    fun handleSecurityCheckFailure(error: Throwable?) {
        executeAfterDelay(2000) { // 2 seconds delay
            alertHandler.hideAlert()
        }

        executeAfterDelay(2500) { // 2.5 seconds delay
            alertHandler.showAlert(
                title = Constants.securityCheckFailureAlertTitle,
                message = Constants.securityCheckAlertFailedMessage(error?.localizedMessage ?: ""),
                showOkButton = true
            )
        }
    }

    fun getCurrentTimestamp(): String {
        val dateFormatter = java.text.SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()
        )
        dateFormatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val currentDate = java.util.Date()
        return dateFormatter.format(currentDate)
    }

    fun <T> decodeJSON(jsonString: String, clazz: Class<T>, onSuccess: (T) -> Unit, onError: (Exception) -> Unit) {
        try {
            val gson = Gson()
            val result = gson.fromJson(jsonString, clazz)
            onSuccess(result)
        } catch (error: Exception) {
            onError(error)
        }
    }
}
