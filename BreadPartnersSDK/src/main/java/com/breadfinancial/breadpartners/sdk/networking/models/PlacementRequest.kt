package com.breadfinancial.breadpartners.sdk.networking.models

import kotlinx.serialization.Serializable

@Serializable
data class PlacementRequest(
    val placements: List<PlacementRequestBody>? = null,
    val brandId: String? = null
)

@Serializable
data class PlacementRequestBody(
    val id: String? = null,
    val context: ContextRequestBody? = null
)


@Serializable
data class ContextRequestBody(
    val SDK_TID: String? = null,
    val ENV: String? = null,
    val RTPS_ID: String? = null,
    val BUYER_ID: String? = null,
    val PREQUAL_ID: String? = null,
    val PREQUAL_CREDIT_LIMIT: String? = null,
    val LOCATION: String? = null,
    val PRICE: Int? = null,
    val EXISTING_CH: Boolean? = null,
    val OVERRIDE_KEY: String? = null,
    val CLIENT_VAR_4: String? = null,
    val channel: String? = null,
    val subchannel: String? = null,
    val CMP: String? = null,
    val ALLOW_CHECKOUT: Boolean? = null,
    val UQP_PARAMS: String? = null,
    val embeddedUrl: String? = null
)
