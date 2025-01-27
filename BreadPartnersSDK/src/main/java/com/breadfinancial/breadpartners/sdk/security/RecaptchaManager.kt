package com.breadfinancial.breadpartners.sdk.security

import android.app.Application
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaException

class RecaptchaManager {

    suspend fun executeReCaptcha(
        context: Application,
        siteKey: String,
        action: RecaptchaAction,
        timeout: Long = 10000L,
        completion: (Result<Result<String>>) -> Unit
    ) {
        try {
            val client = Recaptcha.fetchClient(context, siteKey)
            val token = client.execute(action, timeout)
            println("Recaptcha_Token: $token")
            completion(Result.success(token))
        } catch (error: RecaptchaException) {
            println("Recaptcha_Error: ${error.message}")
            completion(Result.failure(error))
        } catch (exception: Exception) {
            println("Recaptcha_Exception: ${exception.message}")
            completion(Result.failure(exception))
        }
    }
}
