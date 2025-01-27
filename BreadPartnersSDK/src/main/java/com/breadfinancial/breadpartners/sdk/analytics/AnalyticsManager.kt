package com.breadfinancial.breadpartners.sdk.analytics

import android.os.Build
import com.breadfinancial.breadpartners.sdk.analytics.models.Analytics
import com.breadfinancial.breadpartners.sdk.networking.APIClient
import com.breadfinancial.breadpartners.sdk.networking.APIUrl
import com.breadfinancial.breadpartners.sdk.networking.APIUrlType
import com.breadfinancial.breadpartners.sdk.networking.HTTPMethod
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementsResponse
import com.breadfinancial.breadpartners.sdk.utilities.CommonUtils


class AnalyticsManager(
    private val apiClient: APIClient, private val commonUtils: CommonUtils
) {

    // Helper function to send analytics payload
    private fun sendAnalyticsPayload(apiUrl: String, payload: Analytics.Payload) {
        apiClient.request(
            urlString = apiUrl, method = HTTPMethod.POST, body = payload
        ) {}
    }

    // Helper function to create analytics payload
    private fun createAnalyticsPlacementPayload(
        name: String, placementResponse: PlacementsResponse
    ): Analytics.Payload {
        val timestamp = commonUtils.getCurrentTimestamp()
        val osVersion = "Android ${Build.VERSION.RELEASE}" // e.g., "Android 13"
        val deviceModel = Build.MODEL                    // e.g., "Pixel 7"
        val manufacturer = Build.MANUFACTURER           // e.g., "Google"

        return Analytics.Payload(
            name = name, props = Analytics.Props(
                eventProperties = Analytics.EventProperties(
                    placement = Analytics.Placement(
                        id = "03d69ff1-f90c-41b2-8a27-836af7f1eb98",
                        placementContentId = "0",
                        overlayContentId = "0"
                    ), placementContent = Analytics.PlacementContent(
                        id = placementResponse.placementContent?.first()?.id,
                        contentType = placementResponse.placementContent?.first()?.contentType,
                        metadata = placementResponse.placementContent?.first()?.metadata
                    ), metadata = mapOf("location" to "Product"), actionTarget = null
                ), userProperties = emptyMap()
            ), context = Analytics.Context(
                timestamp = timestamp,
                apiKey = "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7",
                browserCtx = Analytics.BrowserCtx(
                    library = Analytics.Library(
                        name = "bread-partners-sdk-android", version = "0.0.1"
                    ),
                    userAgent = "$manufacturer $deviceModel; $osVersion", // Google Pixel 7; Android 13
                    page = Analytics.Page(
                        path = "/collections/living-room/products/benchwright-square-coffee-table",
                        url = "https://aspire-ep-demo.myshopify.com/collections/living-room/products/benchwright-square-coffee-table"
                    )
                ),
                trackingInfo = Analytics.TrackingInfo(
                    userTrackingId = "6f42d67e-cff4-4575-802a-e90a838981bb",
                    sessionTrackingId = "d5cfaf50-f05f-42a8-adea-22d70da25b73"
                )
            )
        )
    }

    // Centralized function to send placement analytics
    private fun sendPlacementAnalytics(
        name: String, placementResponse: PlacementsResponse, apiUrlType: APIUrlType
    ) {
        val payload = createAnalyticsPlacementPayload(name, placementResponse)
        val apiUrl = APIUrl(urlType = apiUrlType).url
        sendAnalyticsPayload(apiUrl, payload)
    }

    // Public function to send view placement analytics
    fun sendViewPlacement(placementResponse: PlacementsResponse) {
        sendPlacementAnalytics("view-placement", placementResponse, APIUrlType.ViewPlacement)
    }

    // Public function to send click placement analytics
    fun sendClickPlacement(placementResponse: PlacementsResponse) {
        sendPlacementAnalytics("click-placement", placementResponse, APIUrlType.ClickPlacement)
    }
}