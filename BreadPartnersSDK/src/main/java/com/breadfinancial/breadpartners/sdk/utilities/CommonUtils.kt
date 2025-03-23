package com.breadfinancial.breadpartners.sdk.utilities

import android.graphics.Color
import android.os.Build
import android.os.Handler
import com.breadfinancial.breadpartners.sdk.core.models.RTPSData
import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.networking.APIUrl
import com.breadfinancial.breadpartners.sdk.networking.APIUrlType
import com.google.gson.Gson
import java.net.URL

class CommonUtils(
    private val handler: Handler, private val alertHandler: AlertHandler
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

    fun <T> decodeJSON(
        jsonString: String, clazz: Class<T>, onSuccess: (T) -> Unit, onError: (Exception) -> Unit
    ) {
        try {
            val gson = Gson()
            val result = gson.fromJson(jsonString, clazz)
            onSuccess(result)
        } catch (error: Exception) {
            onError(error)
        }
    }

    fun buildRTPSWebURL(
        integrationKey: String,
        merchantConfiguration: MerchantConfiguration,
        rtpsConfig: RTPSData,
        prescreenId:Long
    ): URL? {
        val queryParams = mapOf(
            "mockMO" to rtpsConfig.mockResponse?.value,
            "mockPA" to rtpsConfig.mockResponse?.value,
            "mockVL" to rtpsConfig.mockResponse?.value,
            "embedded" to "true",
            "clientKey" to integrationKey,
            "prescreenId" to prescreenId,
            "cardType" to rtpsConfig.cardType,
            "urlPath" to "screen name", // Replace with actual value if necessary
            "firstName" to merchantConfiguration.buyer?.givenName,
            "lastName" to merchantConfiguration.buyer?.familyName,
            "address1" to merchantConfiguration.buyer?.billingAddress?.address1,
            "city" to merchantConfiguration.buyer?.billingAddress?.locality,
            "state" to merchantConfiguration.buyer?.billingAddress?.region,
            "zip" to merchantConfiguration.buyer?.billingAddress?.postalCode,
            "storeNumber" to merchantConfiguration.storeNumber,
            "location" to rtpsConfig.locationType,
            "channel" to rtpsConfig.channel
        )

        return try {
            val urlComponents = URL(APIUrl(urlType = APIUrlType.RTPSWebURL("offer")).url)
            val queryString =
                queryParams.filterValues { it != null }.map { "${it.key}=${it.value}" }
                    .joinToString("&")

            URL("$urlComponents?$queryString")
        } catch (e: Exception) {
            null
        }
    }

    fun darkerColor(color: Int): Int {
        val factor = 0.8f
        val r = (Color.red(color) * factor).toInt()
        val g = (Color.green(color) * factor).toInt()
        val b = (Color.blue(color) * factor).toInt()
        return Color.rgb(r, g, b)
    }

    fun getUserAgent(): String {
        val osVersion = "Android ${Build.VERSION.RELEASE}" // e.g., "Android 13"
        val deviceModel = Build.MODEL                    // e.g., "Pixel 7"
        val manufacturer = Build.MANUFACTURER           // e.g., "Google"
        return "$manufacturer $deviceModel; $osVersion" // Google Pixel 7; Android 13
    }
}
