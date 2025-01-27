package com.breadfinancial.breadpartners.sdk.core.models

import kotlinx.serialization.Serializable

@Serializable
data class RTPSRequest(
    val urlPath: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val address1: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val storeNumber: String? = null,
    val location: String? = null,
    val channel: String? = null,
    val subchannel: String? = null,
    var reCaptchaToken: String? = null,
    val mockResponse: String? = null,
    val overrideConfig: OverrideConfig? = null,
    val prescreenId: String? = null
) {
    @Serializable
    data class OverrideConfig(
        val enhancedPresentment: Boolean? = null
    )
}
