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

import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersEnvironment

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
    val recaptchaEnabledQA: String = "",
    val recaptchaSiteKeyQA: String = "",
    val recaptchaSiteKeyPROD: String = "",
    val recaptchaEnabledPROD: String = "",
    val AEMContent: String = "",
    val clientName: String = "",
    val rsk_UAT_NATIVE_IOS: String = "",
    val rsk_UAT_NATIVE_ANDROID: String = "",
    val rsk_STAGE_NATIVE_IOS: String = "",
    val rsk_STAGE_NATIVE_ANDROID: String = "",
    val rsk_PROD_NATIVE_IOS: String = "",
    val rsk_PROD_NATIVE_ANDROID: String = ""
) {
    fun getRecaptchaKey(environment: BreadPartnersEnvironment): String = when (environment) {
        BreadPartnersEnvironment.UAT -> rsk_UAT_NATIVE_ANDROID
        BreadPartnersEnvironment.STAGE -> rsk_STAGE_NATIVE_ANDROID
        BreadPartnersEnvironment.PROD -> rsk_PROD_NATIVE_ANDROID
    }
}
