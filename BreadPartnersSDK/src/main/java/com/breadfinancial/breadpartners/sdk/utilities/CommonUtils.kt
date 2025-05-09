//------------------------------------------------------------------------------
//  File:          CommonUtils.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.utilities

import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.RTPSData
import com.breadfinancial.breadpartners.sdk.networking.APIUrl
import com.breadfinancial.breadpartners.sdk.networking.APIUrlType
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnersExtensions.takeIfNotEmpty
import com.google.gson.Gson
import java.net.URL

/**
 * Utility class for handling common operations across the SDK.
 */
class CommonUtils(
) {

    /**
     * Returns the current timestamp.
     */
    fun getCurrentTimestamp(): String {
        val dateFormatter = java.text.SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()
        )
        dateFormatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val currentDate = java.util.Date()
        return dateFormatter.format(currentDate)
    }

    /**
     * Safely decodes a JSON string into the specified model class.
     */
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

    /**
     * Constructs the full RTPS Web URL with required query parameters.
     */
    fun buildRTPSWebURL(
        integrationKey: String,
        merchantConfiguration: MerchantConfiguration,
        rtpsConfig: RTPSData,
    ): URL? {
        val queryParams = mapOf(
            "mockMO" to rtpsConfig.mockResponse?.value.takeIfNotEmpty(),
            "mockPA" to rtpsConfig.mockResponse?.value.takeIfNotEmpty(),
            "mockVL" to rtpsConfig.mockResponse?.value.takeIfNotEmpty(),
            "embedded" to "true",
            "clientKey" to integrationKey.takeIfNotEmpty(),
            "prescreenId" to rtpsConfig.prescreenId,
            "cardType" to rtpsConfig.cardType.takeIfNotEmpty(),
            "urlPath" to rtpsConfig.screenName.takeIfNotEmpty(),
            "firstName" to merchantConfiguration.buyer?.givenName.takeIfNotEmpty(),
            "lastName" to merchantConfiguration.buyer?.familyName.takeIfNotEmpty(),
            "address1" to merchantConfiguration.buyer?.billingAddress?.address1.takeIfNotEmpty(),
            "city" to merchantConfiguration.buyer?.billingAddress?.locality.takeIfNotEmpty(),
            "state" to merchantConfiguration.buyer?.billingAddress?.region.takeIfNotEmpty(),
            "zip" to merchantConfiguration.buyer?.billingAddress?.postalCode.takeIfNotEmpty(),
            "storeNumber" to merchantConfiguration.storeNumber.takeIfNotEmpty(),
            "location" to rtpsConfig.locationType?.value?.takeIfNotEmpty(),
            "channel" to rtpsConfig.channel.takeIfNotEmpty(),
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

    /**
     * Returns a darker shade of the given color.
     */
    fun darkerColor(color: Int): Int {
        val factor = 0.8f
        val r = (Color.red(color) * factor).toInt()
        val g = (Color.green(color) * factor).toInt()
        val b = (Color.blue(color) * factor).toInt()
        return Color.rgb(r, g, b)
    }

    /**
     * Generates a simplified User-Agent string based on the device's manufacturer, model, and OS version.
     */
    fun getUserAgent(): String {
        val osVersion = "Android ${Build.VERSION.RELEASE}" // e.g., "Android 13"
        val deviceModel = Build.MODEL                    // e.g., "Pixel 7"
        val manufacturer = Build.MANUFACTURER           // e.g., "Google"
        return "$manufacturer $deviceModel; $osVersion" // Google Pixel 7; Android 13
    }
}
