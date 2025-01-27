package com.breadfinancial.breadpartners.sdk.core.models

import kotlinx.serialization.Serializable

@Serializable
data class RTPSResponse(
    val returnCode: Int,
    val errorMessage: String,
    val errorCode: Int,
    val address1: String? = null,
    val address2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val firstName: String? = null,
    val middleInitial: String? = null,
    val lastName: String? = null,
    val prescreenId: Int,
    val isExpired: Boolean,
    val cardType: String? = null,
    val hasExistingAccount: Boolean? = null,
    val debug: DebugInfo? = null
) {
    @Serializable
    data class DebugInfo(
        val requests: String? = null
    )
}
