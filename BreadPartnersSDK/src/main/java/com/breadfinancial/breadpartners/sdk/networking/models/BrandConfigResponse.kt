package com.breadfinancial.breadpartners.sdk.networking.models

data class BrandConfigResponse(
    val config: Config
)

data class Config(
    val AEMContent: String = "",
    val OVERRIDE_KEY: String = "",
    val clientName: String = "",
    val prodAdServerUrl: String = "",
    val qaAdServerUrl: String = "",
    val recaptchaEnabledQA: String = "",
    val recaptchaSiteKeyQA: String = "",
    val test: String = ""
)
