package com.breadfinancial.breadpartners.sdk.core.extensions

import com.breadfinancial.breadpartners.sdk.core.BreadPartnersSDK
import com.breadfinancial.breadpartners.sdk.core.models.RTPSRequest
import com.breadfinancial.breadpartners.sdk.core.models.RTPSResponse
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PlacementOverlayType
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PopupPlacementModel
import com.breadfinancial.breadpartners.sdk.networking.APIUrl
import com.breadfinancial.breadpartners.sdk.networking.APIUrlType
import com.breadfinancial.breadpartners.sdk.networking.HTTPMethod
import com.breadfinancial.breadpartners.sdk.networking.Result
import com.breadfinancial.breadpartners.sdk.networking.models.BrandConfigResponse
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementsResponse
import com.breadfinancial.breadpartners.sdk.utilities.Constants
import com.google.android.recaptcha.RecaptchaAction
import kotlinx.coroutines.launch

fun BreadPartnersSDK.fetchBrandConfig() {
    coroutineScope.launch {
        val apiUrl = APIUrl(
            urlType = APIUrlType.BrandConfig(placementsConfiguration?.configModel?.brandId ?: "")
        ).url
        apiClient.request(
            urlString = apiUrl,
            method = HTTPMethod.POST,
            body = placementsConfiguration?.configModel
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
                    if (rtpsFlow) {
//                        executeSecurityCheck()
//                        preScreenLookupCall(token= "")
                        fetchPlacementData()
                    } else {
                        fetchPlacementData()
                    }
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

fun BreadPartnersSDK.preScreenLookupCall(token: String) {
    coroutineScope.launch {
        val apiUrl = APIUrl(
            urlType = if (prescreenId == null) APIUrlType.PreScreen else APIUrlType.VirtualLookup
        ).url
        val rtpsRequest = RTPSRequest(
            urlPath = "/cart",
            firstName = "Carol",
            lastName = "Jones",
            address1 = "3075 Loyalty Cir",
            city = "Columbus",
            state = "OH",
            zip = "43219",
            storeNumber = "2009",
            location = "checkout",
            channel = "O",
            subchannel = "M",
            reCaptchaToken = token,
            mockResponse = "success",
            overrideConfig = RTPSRequest.OverrideConfig(
                enhancedPresentment = true
            ),
            prescreenId = null
        )
        rtpsRequest.reCaptchaToken = token

        apiClient.request(
            urlString = apiUrl, method = HTTPMethod.POST, body = rtpsRequest
        ) { result ->
            when (result) {
                is Result.Success -> {
                    commonUtils.decodeJSON(result.data.toString(),
                        RTPSResponse::class.java,
                        onSuccess = { response ->
                            val preScreenLookupResponse = response
                            println("PreScreenID::${preScreenLookupResponse.prescreenId}")
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


fun BreadPartnersSDK.fetchPlacementData() {
    val apiUrl = APIUrl(
        urlType = APIUrlType.GeneratePlacements
    ).url

    coroutineScope.launch {
        apiClient.request(
            urlString = apiUrl,
            method = HTTPMethod.POST,
            body = placementsConfiguration?.configModel
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
                htmlContentRenderer.createPopupOverlay(
                    popupPlacementModel = popupPlacementModel,
                    overlayType = PlacementOverlayType.EMBEDDED_OVERLAY
                )
            } else {
                htmlContentRenderer.handleTextPlacement(
                    placementsResponse, placementsConfiguration!!, thisContext
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