//------------------------------------------------------------------------------
//  File:          RecaptchaManager.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.security

import android.app.Application
import com.breadfinancial.breadpartners.sdk.utilities.Logger
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaException

/**
 * `RecaptchaManager` handles the process of executing a reCAPTCHA for verifying user actions.
 */
class RecaptchaManager(private val logger: Logger) {

    suspend fun executeReCaptcha(
        context: Application,
        siteKey: String,
        action: RecaptchaAction,
        timeout: Long = 10000L,
        completion: (Result<String>) -> Unit
    ) {

        try {
            val client = Recaptcha.fetchClient(context, siteKey)
            val result = client.execute(action, timeout)
            result.onSuccess { token ->
                logger.printLog("Recaptcha_Token: $token")
                completion(Result.success(token))
            }.onFailure {
                logger.printLog("Recaptcha_Error: $it")
                completion(Result.failure(it))
            }

        } catch (error: RecaptchaException) {
            logger.printLog("Recaptcha_Error: ${error.message}")
            completion(Result.failure(error))
        } catch (exception: Exception) {
            logger.printLog("Recaptcha_Exception: ${exception.message}")
            completion(Result.failure(exception))
        }
    }
}
