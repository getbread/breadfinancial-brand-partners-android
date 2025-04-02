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

import android.text.SpannedString
import com.breadfinancial.breadpartners.sdk.core.BreadPartnersSDK
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PlacementOverlayType
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PopupPlacementModel
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
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnersExtensions.takeIfNotEmpty
import com.breadfinancial.breadpartners.sdk.utilities.Constants
import com.google.android.recaptcha.RecaptchaAction
import kotlinx.coroutines.launch

/**
 * Performs a bot behavior check using the Recaptcha v3 SDK
 * to protect against malicious attacks.
 */
fun BreadPartnersSDK.executeSecurityCheck() {
    coroutineScope.launch {
        val siteKey = brandConfiguration?.config?.recaptchaSiteKeyQA
        recaptchaManager.executeReCaptcha(
            context = application,
            siteKey = siteKey ?: "",
            action = RecaptchaAction.custom(customAction = "checkout")
        ) { result ->
            result.onSuccess {
                preScreenLookupCall(token = it)
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
fun BreadPartnersSDK.preScreenLookupCall(token: String) {
    coroutineScope.launch {
        val apiUrl = APIUrl(
            urlType = if (placementsConfiguration?.rtpsData?.prescreenId == null) APIUrlType.PreScreen else APIUrlType.VirtualLookup
        ).url
        val rtpsRequestBuilder = RTPSRequestBuilder(
            merchantConfiguration!!, placementsConfiguration?.rtpsData!!, reCaptchaToken = token
        )
        val rtpsRequest = rtpsRequestBuilder.build()
        val headers = mapOf(
            Constants.headerClientKey to integrationKey,
            Constants.headerRequestedWithKey to Constants.headerRequestedWithValue
        )
        apiClient.request(
            urlString = apiUrl, method = HTTPMethod.POST, body = rtpsRequest, headers = headers
        ) { result ->
            when (result) {
                is Result.Success -> {
                    commonUtils.decodeJSON(result.data.toString(),
                        RTPSResponse::class.java,
                        onSuccess = { response ->
                            val returnResultType = response.returnCode
                            val prescreenResult = getPrescreenResult(returnResultType)
                            prescreenId = response.prescreenId
                            logger.printLog("PreScreenID:Result: $prescreenResult")
                            logger.printLog("PreScreenID::$prescreenId")

                            /**
                             * This call runs in the background without user interaction.
                             * If the result is not "approved" or prescreenID is null,
                             * we simply return without taking any further action.
                             */
                            if (prescreenResult != PrescreenResult.APPROVED || prescreenId == null) {
                                callback(
                                    BreadPartnerEvent.SdkError(
                                        error = Exception(
                                            "Error: PreScreen Result $prescreenResult"
                                        )
                                    )
                                )
                                return@decodeJSON
                            }

                            fetchRTPSData()
                        },
                        onError = { error ->
                            alertHandler.showAlert(
                                title = Constants.nativeSDKAlertTitle(),
                                message = Constants.catchError(
                                    error.localizedMessage?.toString() ?: ""
                                ),
                                showOkButton = true
                            )
                        })
                }

                is Result.Failure -> {
                    alertHandler.showAlert(
                        title = Constants.nativeSDKAlertTitle(), message = Constants.catchError(
                            result.error.localizedMessage ?: "Unknown error"
                        ), showOkButton = true
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
fun BreadPartnersSDK.fetchRTPSData() {
    val apiUrl = APIUrl(
        urlType = APIUrlType.GeneratePlacements
    ).url

    val rtpsWebURL = commonUtils.buildRTPSWebURL(
        integrationKey = integrationKey,
        merchantConfiguration = merchantConfiguration!!,
        rtpsConfig = placementsConfiguration?.rtpsData!!,
        prescreenId = prescreenId
    )?.toString()

    val placementRequest = PlacementRequest(
        placements = listOf(
            PlacementRequestBody(
                context = ContextRequestBody(
                    ENV = merchantConfiguration?.env?.value.takeIfNotEmpty(),
                    LOCATION = "RTPS-Approval",
                    embeddedUrl = rtpsWebURL
                )
            )
        ), brandId = integrationKey
    )

    coroutineScope.launch {
        apiClient.request(
            urlString = apiUrl, method = HTTPMethod.POST, body = placementRequest
        ) { result ->
            when (result) {
                is Result.Success -> {
                    try {
                        handleRTPSResponse(result.data)
                    } catch (error: Exception) {
                        alertHandler.showAlert(
                            title = Constants.nativeSDKAlertTitle(),
                            message = Constants.catchError(message = "${error.message}"),
                            showOkButton = true
                        )
                    }
                }

                is Result.Failure -> {
                    alertHandler.showAlert(
                        title = Constants.nativeSDKAlertTitle(),
                        message = Constants.generatePlacementAPIError(message = "${result.error}"),
                        showOkButton = true
                    )
                }
            }
        }
    }
}

/**
 * Handle RTPS data to be displayed as a Popup view
 * in the brand partner's UI.
 */
fun BreadPartnersSDK.handleRTPSResponse(response: Any) {
    commonUtils.decodeJSON(response.toString(),
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
            htmlContentRenderer?.createPopupOverlay(
                popupPlacementModel = popupPlacementModel,
                overlayType = PlacementOverlayType.EMBEDDED_OVERLAY
            )
        },
        onError = { error ->
            alertHandler.showAlert(
                title = Constants.nativeSDKAlertTitle(),
                message = Constants.catchError(message = "${error.message}"),
                showOkButton = true
            )
        })
}
