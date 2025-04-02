//------------------------------------------------------------------------------
//  File:          PlacementApiExtension.kt
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

import com.breadfinancial.breadpartners.sdk.core.BreadPartnersSDK
import com.breadfinancial.breadpartners.sdk.networking.APIUrl
import com.breadfinancial.breadpartners.sdk.networking.APIUrlType
import com.breadfinancial.breadpartners.sdk.networking.HTTPMethod
import com.breadfinancial.breadpartners.sdk.networking.Result
import com.breadfinancial.breadpartners.sdk.networking.models.BrandConfigResponse
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementsResponse
import com.breadfinancial.breadpartners.sdk.networking.requestbuilders.PlacementRequestBuilder
import com.breadfinancial.breadpartners.sdk.utilities.Constants
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
 * Fetches placement data to be displayed as a text view with a clickable button
 * in the brand partner's UI.
 */
fun BreadPartnersSDK.fetchPlacementData() {
    val apiUrl = APIUrl(
        urlType = APIUrlType.GeneratePlacements
    ).url
    val builder = PlacementRequestBuilder(
        integrationKey = integrationKey,
        merchantConfiguration,
        placementsConfiguration?.placementData
    )
    val placementRequest = builder.build()
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
            if (openPlacementExperience) {
                val popupPlacementHTMLContent = placementsResponse.placementContent?.firstOrNull()
                try {
                    htmlContentParser.extractPopupPlacementModel(
                        popupPlacementHTMLContent?.contentData?.htmlContent ?: ""
                    )?.let { popupPlacementModel ->
                        logger.logPopupPlacementModelDetails(popupPlacementModel)
                        val overlayType =
                            htmlContentParser.handleOverlayType(popupPlacementModel.overlayType)
                        if (overlayType != null) {
                            htmlContentRenderer?.createPopupOverlay(
                                popupPlacementModel, overlayType
                            )
                        } else {
                            alertHandler.showAlert(
                                title = Constants.nativeSDKAlertTitle(),
                                message = Constants.missingPopupPlacementError,
                                showOkButton = true
                            )
                        }
                    }
                } catch (error: Exception) {
                    alertHandler.showAlert(
                        title = Constants.nativeSDKAlertTitle(),
                        message = Constants.catchError(message = "${error.message}"),
                        showOkButton = true
                    )
                }
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