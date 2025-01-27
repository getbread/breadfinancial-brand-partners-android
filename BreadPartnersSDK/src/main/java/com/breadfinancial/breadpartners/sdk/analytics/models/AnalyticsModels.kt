package com.breadfinancial.breadpartners.sdk.analytics.models

import com.breadfinancial.breadpartners.sdk.networking.models.MetadataModel

data class Analytics(
    val placementContent: PlacementContent?,
    val placement: Placement?,
    val eventProperties: EventProperties?,
    val props: Props?,
    val browserCtx: BrowserCtx?,
    val trackingInfo: TrackingInfo?,
    val context: Context?,
    val payload: Payload?
) {
    data class PlacementContent(
        val id: String?, val contentType: String?, val metadata: MetadataModel?
    )

    data class Placement(
        val id: String?, val placementContentId: String?, val overlayContentId: String?
    )

    data class EventProperties(
        val placement: Placement?,
        val placementContent: PlacementContent?,
        val metadata: Map<String, String>?,
        val actionTarget: String?
    )

    data class Props(
        val eventProperties: EventProperties?, val userProperties: Map<String, String>?
    )

    data class BrowserCtx(
        val library: Library?, val userAgent: String?, val page: Page?
    )

    data class Library(
        val name: String?, val version: String?
    )

    data class Page(
        val path: String?, val url: String?
    )

    data class TrackingInfo(
        val userTrackingId: String?, val sessionTrackingId: String?
    )

    data class Context(
        val timestamp: String?,
        val apiKey: String?,
        val browserCtx: BrowserCtx?,
        val trackingInfo: TrackingInfo?
    )

    data class Payload(
        val name: String?, val props: Props?, val context: Context?
    )
}
