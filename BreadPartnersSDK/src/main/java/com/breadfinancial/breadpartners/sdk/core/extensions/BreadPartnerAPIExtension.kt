package com.breadfinancial.breadpartners.sdk.core.extensions

import com.breadfinancial.breadpartners.sdk.core.BreadPartnersSDK
import com.breadfinancial.breadpartners.sdk.core.models.LocationType
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PlacementOverlayType
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PopupPlacementModel
import com.breadfinancial.breadpartners.sdk.networking.APIUrl
import com.breadfinancial.breadpartners.sdk.networking.APIUrlType
import com.breadfinancial.breadpartners.sdk.networking.HTTPMethod
import com.breadfinancial.breadpartners.sdk.networking.Result
import com.breadfinancial.breadpartners.sdk.networking.models.BrandConfigResponse
import com.breadfinancial.breadpartners.sdk.networking.models.ContextRequestBody
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequest
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequestBody
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementsResponse
import com.breadfinancial.breadpartners.sdk.networking.models.RTPSResponse
import com.breadfinancial.breadpartners.sdk.networking.requestbuilders.PlacementRequestBuilder
import com.breadfinancial.breadpartners.sdk.networking.requestbuilders.RTPSRequestBuilder
import com.breadfinancial.breadpartners.sdk.utilities.Constants
import com.google.android.recaptcha.RecaptchaAction
import kotlinx.coroutines.launch

/**
 * Retrieves brand-specific configurations, such as the Recaptcha key.
 */
fun BreadPartnersSDK.fetchBrandConfig() {
    coroutineScope.launch {
        val apiUrl = APIUrl(
            urlType = APIUrlType.BrandConfig(integrationKey)
        ).url
        apiClient.request(
            urlString = apiUrl, method = HTTPMethod.GET, body = null
        ) { result ->
            when (result) {
                is Result.Success -> {
                    commonUtils.decodeJSON(result.data.toString(),
                        BrandConfigResponse::class.java,
                        onSuccess = { response ->
                            brandConfiguration = response
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
 * Performs a bot behavior check using the Recaptcha v3 SDK
 * to protect against malicious attacks.
 */
fun BreadPartnersSDK.executeSecurityCheck() {
    coroutineScope.launch {
//        val siteKey = brandConfiguration?.config?.recaptchaSiteKeyQA
        val siteKey = "6Ldai6wqAAAAAAaYMIgzUKlNAO3zTLHnOL7Fce0w"
        recaptchaManager.executeReCaptcha(
            context = application,
            siteKey = siteKey,
            action = RecaptchaAction.custom(customAction = "checkout")
        ) { result ->
            result.onSuccess {
                commonUtils.executeAfterDelay(2000) {
                    preScreenLookupCall(it.toString())
                    alertHandler.hideAlert()
                }
            }
            result.onFailure { error ->
                commonUtils.handleSecurityCheckFailure(error)
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
            urlType = if (prescreenId == null) APIUrlType.PreScreen else APIUrlType.VirtualLookup
        ).url
        val rtpsRequestBuilder = RTPSRequestBuilder(
            merchantConfiguration!!, placementsConfiguration?.rtpsConfig!!
        )
        val rtpsRequest = rtpsRequestBuilder.build()
        rtpsRequest.reCaptchaToken = token

        apiClient.request(
            urlString = apiUrl, method = HTTPMethod.POST, body = rtpsRequest
        ) { result ->
            when (result) {
                is Result.Success -> {
                    commonUtils.decodeJSON(result.data.toString(),
                        RTPSResponse::class.java,
                        onSuccess = { response ->
                            println("PreScreenID::${response.prescreenId}")
                            fetchPlacementData()
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
                    executeSecurityCheck()
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
fun BreadPartnersSDK.fetchPlacementData() {
    val apiUrl = APIUrl(
        urlType = APIUrlType.GeneratePlacements
    ).url
    val placementRequest: Any?
    if (placementsConfiguration?.placementData != null) {
        val builder = PlacementRequestBuilder(
            integrationKey = integrationKey,
            merchantConfiguration,
            placementsConfiguration?.placementData
        )
        placementRequest = builder.build()
    } else {
        val rtpsWebURL = commonUtils.buildRTPSWebURL(
            integrationKey = integrationKey,
            merchantConfiguration = merchantConfiguration!!,
            rtpsConfig = placementsConfiguration?.rtpsConfig!!
        )?.toString()

        val location = when (placementsConfiguration?.rtpsConfig?.locationType) {
            LocationType.CHECKOUT -> "RTPS-Approval"
            else -> ""
        }

        placementRequest = PlacementRequest(
            placements = listOf(
                PlacementRequestBody(
                    context = ContextRequestBody(
                        ENV = "", LOCATION = location, embeddedUrl = rtpsWebURL
                    )
                )
            ), brandId = integrationKey
        )
    }

    coroutineScope.launch {
        apiClient.request(
            urlString = apiUrl, method = HTTPMethod.POST, body = placementRequest
        ) { result ->
            when (result) {
                is Result.Success -> {
                    try {
                        handlePlacementResponse(result.data)
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

fun BreadPartnersSDK.handlePlacementResponse(response: Any) {
    commonUtils.decodeJSON(response.toString(),
        PlacementsResponse::class.java,
        onSuccess = { placementsResponse ->
            if (rtpsFlow) {
                val popupPlacementModel = PopupPlacementModel(
                    overlayType = "EMBEDDED_OVERLAY",
                    location = placementsResponse.placements?.first()?.renderContext?.LOCATION.toString(),
                    brandLogoUrl = "",
                    webViewUrl = placementsResponse.placements?.first()?.renderContext?.embeddedUrl
                        ?: "",
                    overlayTitle = "",
                    overlaySubtitle = "",
                    overlayContainerBarHeading = "",
                    bodyHeader = "",
                    dynamicBodyModel = PopupPlacementModel.DynamicBodyModel(
                        bodyDiv = mapOf(
                            "" to PopupPlacementModel.DynamicBodyContent(
                                tagValuePairs = mapOf(
                                    "" to ""
                                )
                            )
                        )
                    ),
                    disclosure = "",
                    primaryActionButtonAttributes = null
                )
                htmlContentRenderer?.createPopupOverlay(
                    popupPlacementModel = popupPlacementModel,
                    overlayType = PlacementOverlayType.EMBEDDED_OVERLAY
                )
            } else {
                htmlContentRenderer?.handleTextPlacement(
                    placementsResponse, thisContext
                )
            }
        },
        onError = { error ->
            alertHandler.showAlert(
                title = Constants.nativeSDKAlertTitle(),
                message = Constants.catchError(message = "${error.message}"),
                showOkButton = true
            )
        })
}