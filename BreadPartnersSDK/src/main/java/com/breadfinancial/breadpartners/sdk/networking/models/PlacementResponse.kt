package com.breadfinancial.breadpartners.sdk.networking.models

import kotlinx.serialization.Serializable

@Serializable
data class PlacementsResponse(
    val placements: List<PlacementsModel>? = null,
    val placementContent: List<PlacementContentModel>? = null
)

@Serializable
data class PlacementsModel(
    val id: String? = null,
    val content: PlacementContentReferenceModel? = null,
    val renderContext: RenderContextModel? = null
)

@Serializable
data class PlacementContentReferenceModel(
    val contentId: String? = null
)

@Suppress("CASE_INSENSITIVE_FILE_NAMES")
@Serializable
data class RenderContextModel(
    val LOCATION: String? = null,
    val subchannel: String? = null,
    val RTPS_ID: String? = null,
    val PREQUAL_ID: String? = null,
    val PRICE: Int? = null,
    val DATETIME: String? = null,
    val SDK_TID: String? = null,
    val BUYER_ID: String? = null,
    val channel: String? = null,
    val PREQUAL_CREDIT_LIMIT: String? = null,
    val ENV: String? = null,
    val ALLOW_CHECKOUT: Boolean? = null,
    val embeddedUrl: String? = null
)

@Serializable
data class PlacementContentModel(
    val id: String? = null,
    val contentType: String? = null,
    val contentData: ContentDataModel? = null,
    val metadata: MetadataModel? = null
)

@Serializable
data class ContentDataModel(
    val htmlContent: String? = null
)

@Serializable
data class MetadataModel(
    val placementId: String? = null,
    val productType: String? = null,
    val messageId: String? = null,
    val templateId: String? = null
)
