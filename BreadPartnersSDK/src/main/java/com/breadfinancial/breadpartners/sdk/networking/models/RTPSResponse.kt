//------------------------------------------------------------------------------
//  File:          RTPSResponse.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.networking.models

enum class PrescreenResult {
    // Account found from virtual lookup
    ACCOUNT_FOUND,
    // Has been pre-approved
    APPROVED,
    NO_HIT,
    // Not pre-approved but should/could apply
    MAKE_OFFER,
    ACKNOWLEDGE
}

val prescreenResultMap: Map<String, PrescreenResult> = mapOf(
    "0" to PrescreenResult.ACCOUNT_FOUND,
    "01" to PrescreenResult.APPROVED,
    "10" to PrescreenResult.NO_HIT,
    "11" to PrescreenResult.MAKE_OFFER,
    "12" to PrescreenResult.ACKNOWLEDGE
)

fun getPrescreenResult(apiResponse: String): PrescreenResult {
    return prescreenResultMap[apiResponse] ?: PrescreenResult.NO_HIT
}

data class RTPSResponse(
    val returnCode: String,
    val prescreenId: Long
)