//------------------------------------------------------------------------------
//  File:          PopupAPIExtension.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.extensions

import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.PopupDialog
import com.breadfinancial.breadpartners.sdk.networking.APIUrl
import com.breadfinancial.breadpartners.sdk.networking.APIUrlType
import com.breadfinancial.breadpartners.sdk.networking.HTTPMethod
import com.breadfinancial.breadpartners.sdk.networking.Result
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequest
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequestBody
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementsResponse
import com.breadfinancial.breadpartners.sdk.networking.requestbuilders.PlacementRequestBuilder
import com.breadfinancial.breadpartners.sdk.utilities.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fetches placement data after the popup is displayed.
 */
fun PopupDialog.fetchWebViewPlacement() {

    val builder = PlacementRequestBuilder(
        integrationKey, merchantConfiguration, placementsConfiguration?.placementData
    )
    val placementRequest = builder.build()

    val request = PlacementRequest(
        placements = listOf(
            PlacementRequestBody(
                id = popupModel.primaryActionButtonAttributes?.dataContentFetch,
                context = placementRequest.placements?.firstOrNull()?.context
            )
        ), brandId = integrationKey
    )

    fetchData(request)
}

/**
 * Fetches placement data based on the provided request body.
 */
fun PopupDialog.fetchData(requestBody: PlacementRequest) {
    val apiUrl = APIUrl(
        urlType = APIUrlType.GeneratePlacements
    ).url
    CoroutineScope(Dispatchers.Main).launch {
        apiClient.request(
            urlString = apiUrl, body = requestBody, method = HTTPMethod.POST
        ) { result ->
            when (result) {
                is Result.Success -> {
                    commonUtils.decodeJSON(result.data.toString(),
                        PlacementsResponse::class.java,
                        onSuccess = { placementsResponse ->
                            val popupPlacementHTMLContent =
                                placementsResponse.placementContent?.firstOrNull()
                            try {
                                htmlContentParser.extractPopupPlacementModel(
                                    popupPlacementHTMLContent?.contentData?.htmlContent ?: ""
                                )?.let { popupPlacementModel ->
                                    logger.logPopupPlacementModelDetails(popupPlacementModel)
                                    webViewPlacementModel = popupPlacementModel
                                }
                            } catch (error: Exception) {
                                alertHandler.showAlert(
                                    title = Constants.nativeSDKAlertTitle(),
                                    message = Constants.catchError(message = "${error.message}"),
                                    showOkButton = true
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