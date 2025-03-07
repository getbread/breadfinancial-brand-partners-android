package com.breadfinancial.breadpartners.sdk.networking.models

enum class PrescreenResult {
    // Has been pre-approved
    APPROVED,
    NO_HIT,
    // Not pre-approved but should/could apply
    MAKE_OFFER,
    ACKNOWLEDGE
}

val prescreenResultMap: Map<String, PrescreenResult> = mapOf(
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
    val prescreenId: Int
)