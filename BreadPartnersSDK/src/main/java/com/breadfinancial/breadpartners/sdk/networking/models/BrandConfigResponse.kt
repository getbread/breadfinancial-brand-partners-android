//------------------------------------------------------------------------------
//  File:          BrandConfigResponse.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

@file:Suppress("PropertyName")

package com.breadfinancial.breadpartners.sdk.networking.models

/**
 * Data model represents brand-specific configuration response.
 */
data class BrandConfigResponse(
    val config: Config
)

/**
 * Holds various configuration values used for brand customization and environment handling.
 */
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
