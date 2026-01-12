//------------------------------------------------------------------------------
//  File:          RTPSApiExtension.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.core.extensions

import android.content.Context
import android.text.SpannedString
import com.breadfinancial.breadpartners.sdk.core.BreadPartnersSDK
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersEnvironment
import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import com.breadfinancial.breadpartners.sdk.htmlhandling.HTMLContentRenderer
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PlacementOverlayType
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PopupPlacementModel
import com.breadfinancial.breadpartners.sdk.htmlhandling.ChallengeDialog
import com.breadfinancial.breadpartners.sdk.networking.APIClient
import com.breadfinancial.breadpartners.sdk.networking.APIUrl
import com.breadfinancial.breadpartners.sdk.networking.APIUrlType
import com.breadfinancial.breadpartners.sdk.networking.HTTPMethod
import com.breadfinancial.breadpartners.sdk.networking.Result
import com.breadfinancial.breadpartners.sdk.networking.models.ContextRequestBody
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequest
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequestBody
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementsResponse
import com.breadfinancial.breadpartners.sdk.networking.models.PrescreenResult
import com.breadfinancial.breadpartners.sdk.networking.models.RTPSResponse
import com.breadfinancial.breadpartners.sdk.networking.models.getPrescreenResult
import com.breadfinancial.breadpartners.sdk.networking.requestbuilders.RTPSRequestBuilder
import com.breadfinancial.breadpartners.sdk.security.RecaptchaManager
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnersExtensions.takeIfNotEmpty
import com.breadfinancial.breadpartners.sdk.utilities.CommonUtils
import com.breadfinancial.breadpartners.sdk.utilities.Constants
import com.breadfinancial.breadpartners.sdk.utilities.Logger
import com.google.android.recaptcha.RecaptchaAction
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Performs a bot behavior check using the Recaptcha v3 SDK
 * to protect against malicious attacks.
 */
fun BreadPartnersSDK.executeSecurityCheck(
    merchantConfiguration: MerchantConfiguration,
    placementsConfiguration: PlacementsConfiguration,
    viewContext: Context,
    callback: (BreadPartnerEvent) -> Unit
) {
    GlobalScope.launch {
        val siteKey = brandConfiguration?.config?.getRecaptchaKey(
            merchantConfiguration.env ?: BreadPartnersEnvironment.PROD
        )
        RecaptchaManager.init(Logger).executeReCaptcha(
            context = application,
            siteKey = siteKey ?: "",
            action = RecaptchaAction.custom(customAction = "checkout")
        ) { result ->
            result.onSuccess {
                preScreenLookupCall(
                    merchantConfiguration = merchantConfiguration,
                    placementsConfiguration = placementsConfiguration,
                    viewContext = viewContext,
                    callback = callback,
                    token = it
                )
            }
            result.onFailure {
                return@executeReCaptcha
            }
        }
    }
}

/**
 * Once the Recaptcha token is obtained, makes the pre-screen lookup API call.
 *
 * - If `prescreenId` was previously saved by the brand partner when calling the pre-screen endpoint,
 *   triggers `virtualLookup`.
 * - Otherwise, calls the pre-screen endpoint to fetch the `prescreenId`.
 * - Both endpoints require user details to build the request payload.
 */
fun BreadPartnersSDK.preScreenLookupCall(
    merchantConfiguration: MerchantConfiguration,
    placementsConfiguration: PlacementsConfiguration,
    viewContext: Context,
    callback: (BreadPartnerEvent) -> Unit,
    token: String
) {
    val apiUrl = APIUrl(
        urlType = if (placementsConfiguration.rtpsData?.prescreenId == null) APIUrlType.PreScreen else APIUrlType.VirtualLookup
    ).url
    val rtpsRequestBuilder = RTPSRequestBuilder(
        merchantConfiguration, placementsConfiguration.rtpsData!!, reCaptchaToken = token
    )
    val rtpsRequest = rtpsRequestBuilder.build()
    val headers = mapOf(
        Constants.headerClientKey to integrationKey,
        Constants.headerRequestedWithKey to Constants.headerRequestedWithValue
    )
    APIClient().request(
        urlString = apiUrl, method = HTTPMethod.POST, body = rtpsRequest, headers = headers
    ) { result ->
        when (result) {
            is Result.Success -> {
                CommonUtils().decodeJSON(result.data.toString(),
                    RTPSResponse::class.java,
                    onSuccess = { response ->
                        val returnResultType = response.returnCode
                        val prescreenResult = getPrescreenResult(returnResultType)
                        placementsConfiguration.rtpsData.prescreenId = response.prescreenId
                        Logger.printLog("PreScreenID:Result: $prescreenResult")

                        /**
                         * This call runs in the background without user interaction.
                         * If the result is not "approved" or prescreenID is null,
                         * we simply return without taking any further action.
                         */
                        if (prescreenResult != PrescreenResult.APPROVED || placementsConfiguration.rtpsData.prescreenId == null) {
                            callback(
                                BreadPartnerEvent.SdkError(
                                    error = Exception(
                                        "Error: PreScreen Result $prescreenResult"
                                    )
                                )
                            )
                        } else {
                            fetchRTPSData(
                                merchantConfiguration, placementsConfiguration, viewContext, callback
                            )
                        }
                    },
                    onError = { error ->
                        callback(
                            BreadPartnerEvent.SdkError(
                                error = Exception(error.message)
                            )
                        )
                    })
            }

            is Result.Failure -> {
                // Check if this is an security challenge
                val errorMessage = result.error.message ?: ""
                if (errorMessage.startsWith("Security challenge detected:", ignoreCase = true)) {
                    val htmlContent =
                        errorMessage.removePrefix("Security challenge detected:").trim()

                    val challengeDialog = ChallengeDialog(
                        htmlContent = htmlContent,
                        baseUrl = apiUrl.substringBefore("/api"),
                        onComplete = {
                            // Retry the API call after challenge completion
                            preScreenLookupCall(
                                merchantConfiguration,
                                placementsConfiguration,
                                viewContext,
                                callback,
                                token
                            )
                        }
                    )

                    callback(BreadPartnerEvent.RenderPopupView(dialogFragment = challengeDialog))
                } else {
                    callback(
                        BreadPartnerEvent.SdkError(
                            error = Exception(errorMessage)
                        )
                    )
                }
            }
        }
    }
}

/**
 * Fetches placement data to be displayed as a text view with a clickable button
 * in the brand partner's UI.
 */
fun BreadPartnersSDK.fetchRTPSData(
    merchantConfiguration: MerchantConfiguration,
    placementsConfiguration: PlacementsConfiguration,
    viewContext: Context,
    callback: (BreadPartnerEvent) -> Unit
) {
    val apiUrl = APIUrl(
        urlType = APIUrlType.GeneratePlacements
    ).url

    val rtpsWebURL = CommonUtils().buildRTPSWebURL(
        integrationKey = integrationKey,
        merchantConfiguration = merchantConfiguration,
        rtpsConfig = placementsConfiguration.rtpsData!!,
    )?.toString()

    val placementRequest = PlacementRequest(
        placements = listOf(
            PlacementRequestBody(
                context = ContextRequestBody(
                    ENV = merchantConfiguration.env?.value.takeIfNotEmpty(),
                    LOCATION = "RTPS-Approval",
                    embeddedUrl = rtpsWebURL
                )
            )
        ), brandId = integrationKey
    )

    APIClient().request(
        urlString = apiUrl, method = HTTPMethod.POST, body = placementRequest
    ) { result ->
        when (result) {
            is Result.Success -> {
                try {
                    handleRTPSResponse(
                        merchantConfiguration,
                        placementsConfiguration,
                        viewContext,
                        callback,
                        result.data
                    )
                } catch (error: Exception) {
                    callback(
                        BreadPartnerEvent.SdkError(
                            error = Exception(
                                error.message
                            )
                        )
                    )
                }
            }

            is Result.Failure -> {
                callback(
                    BreadPartnerEvent.SdkError(
                        error = Exception(
                            Constants.generatePlacementAPIError(message = "${result.error}")
                        )
                    )
                )
            }
        }
    }
}

/**
 * Handle RTPS data to be displayed as a Popup view
 * in the brand partner's UI.
 */
fun BreadPartnersSDK.handleRTPSResponse(
    merchantConfiguration: MerchantConfiguration,
    placementsConfiguration: PlacementsConfiguration,
    viewContext: Context,
    callback: (BreadPartnerEvent) -> Unit,
    response: Any
) {
    CommonUtils().decodeJSON(response.toString(),
        PlacementsResponse::class.java,
        onSuccess = { placementsResponse ->
            val popupPlacementModel = PopupPlacementModel(
                overlayType = "EMBEDDED_OVERLAY",
                location = placementsResponse.placements?.first()?.renderContext?.LOCATION.toString(),
                brandLogoUrl = "",
                webViewUrl = placementsResponse.placements?.first()?.renderContext?.embeddedUrl
                    ?: "",
                overlayTitle = SpannedString(""),
                overlaySubtitle = SpannedString(""),
                overlayContainerBarHeading = SpannedString(""),
                bodyHeader = SpannedString(""),
                dynamicBodyModel = PopupPlacementModel.DynamicBodyModel(
                    bodyDiv = mapOf(
                        "" to PopupPlacementModel.DynamicBodyContent(
                            tagValuePairs = mapOf(
                                "" to SpannedString("")
                            )
                        )
                    )
                ),
                disclosure = SpannedString(""),
                primaryActionButtonAttributes = null
            )
            HTMLContentRenderer(
                integrationKey,
                merchantConfiguration,
                placementsConfiguration,
                brandConfiguration,
                false,
                callback
            ).createPopupOverlay(
                popupPlacementModel = popupPlacementModel,
                overlayType = PlacementOverlayType.EMBEDDED_OVERLAY
            )
        },
        onError = { error ->
            callback(
                BreadPartnerEvent.SdkError(
                    error = Exception(
                        error.message
                    )
                )
            )
        })
}
