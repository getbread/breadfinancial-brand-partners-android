package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.extensions

import com.breadfinancial.breadpartners.sdk.core.BreadPartnersSDK
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.PopupDialog
import com.breadfinancial.breadpartners.sdk.networking.APIUrl
import com.breadfinancial.breadpartners.sdk.networking.APIUrlType
import com.breadfinancial.breadpartners.sdk.networking.HTTPMethod
import com.breadfinancial.breadpartners.sdk.networking.Result
import com.breadfinancial.breadpartners.sdk.networking.models.ContextRequestBody
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequest
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequestBody
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementsResponse
import com.breadfinancial.breadpartners.sdk.utilities.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun PopupDialog.fetchWebViewPlacement() {
    val configuration = BreadPartnersSDK.getInstance().placementsConfiguration
    val request = PlacementRequest(
        placements = listOf(
            PlacementRequestBody(
                id = popupModel.primaryActionButtonAttributes?.dataContentFetch,
                context = ContextRequestBody(
                    SDK_TID = configuration!!.configModel.placements?.firstOrNull()?.context?.SDK_TID,
                    ENV = configuration.configModel.placements?.firstOrNull()?.context?.ENV,
                    LOCATION = configuration.configModel.placements?.firstOrNull()?.context?.LOCATION,
                    channel = configuration.configModel.placements?.firstOrNull()?.context?.channel,
                    subchannel = configuration.configModel.placements?.firstOrNull()?.context?.subchannel,
                    ALLOW_CHECKOUT = configuration.configModel.placements?.firstOrNull()?.context?.ALLOW_CHECKOUT
                )
            )
        ), brandId = configuration.configModel.brandId
    )

    fetchData(request)
}

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