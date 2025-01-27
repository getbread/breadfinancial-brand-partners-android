package com.breadfinancial.breadpartners.sdk.htmlhandling

import androidx.appcompat.app.AppCompatActivity
import com.breadfinancial.breadpartners.sdk.analytics.AnalyticsManager
import com.breadfinancial.breadpartners.sdk.core.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.InteractiveText
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.PopupDialog
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PlacementActionType
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PlacementOverlayType
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PopupPlacementModel
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.TextPlacementModel
import com.breadfinancial.breadpartners.sdk.networking.APIClient
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementsResponse
import com.breadfinancial.breadpartners.sdk.utilities.AlertHandler
import com.breadfinancial.breadpartners.sdk.utilities.CommonUtils
import com.breadfinancial.breadpartners.sdk.utilities.Constants
import com.breadfinancial.breadpartners.sdk.utilities.Logger

class HTMLContentRenderer(
    private val htmlContentParser: HTMLContentParser,
    private val analyticsManager: AnalyticsManager,
    private val alertHandler: AlertHandler,
    private val commonUtils: CommonUtils,
    private val logger: Logger,
    private val apiClient: APIClient,
    private val callback: (BreadPartnerEvent) -> Unit?
) {

    fun handleTextPlacement(
        responseModel: PlacementsResponse,
        sdkConfiguration: PlacementsConfiguration,
        thisContext: AppCompatActivity
    ) {
        val indexOfPlacement = 0
        val textPlacementModel = htmlContentParser.extractTextPlacementModel(
            htmlContent = responseModel.placementContent?.get(indexOfPlacement)?.contentData?.htmlContent
                ?: ""
        )
        logger.logTextPlacementModelDetails(textPlacementModel)
        analyticsManager.sendViewPlacement(responseModel)
        createTextView(textPlacementModel, responseModel, sdkConfiguration, thisContext)
    }

    private fun createTextView(
        textPlacementModel: TextPlacementModel,
        responseModel: PlacementsResponse,
        sdkConfiguration: PlacementsConfiguration,
        thisContext: AppCompatActivity
    ) {
        val interactiveText = InteractiveText(thisContext)
        interactiveText.configure(textPlacementModel, sdkConfiguration.textPlacementStyling!!) {
            handleLinkInteraction(textPlacementModel, responseModel)
        }
        callback(BreadPartnerEvent.RenderTextView(view = interactiveText))
    }

    private fun handleLinkInteraction(
        textPlacementModel: TextPlacementModel, responseModel: PlacementsResponse
    ) {
        val actionType =
            textPlacementModel.actionType?.let { htmlContentParser.handleActionType(it) }
        if (actionType == PlacementActionType.SHOW_OVERLAY) {
            handlePopupPlacement(textPlacementModel, responseModel)
        } else {
            showAlert(Constants.nativeSDKAlertTitle(), Constants.missingTextPlacementError)
        }
    }

    private fun handlePopupPlacement(
        textPlacementModel: TextPlacementModel, responseModel: PlacementsResponse
    ) {
        val popupPlacementHTMLContent = responseModel.placementContent?.find {
            it.id == textPlacementModel.actionContentId
        }

        val popupPlacementModel = htmlContentParser.extractPopupPlacementModel(
            popupPlacementHTMLContent?.contentData?.htmlContent ?: ""
        ) ?: return showAlert(
            Constants.nativeSDKAlertTitle(), Constants.popupPlacementParsingError
        )

        logger.logPopupPlacementModelDetails(popupPlacementModel)

        val overlayType = htmlContentParser.handleOverlayType(popupPlacementModel.overlayType)
            ?: return showAlert(
                Constants.nativeSDKAlertTitle(), Constants.missingPopupPlacementError
            )
        analyticsManager.sendClickPlacement(responseModel)

        createPopupOverlay(popupPlacementModel, overlayType)
    }

    fun createPopupOverlay(
        popupPlacementModel: PopupPlacementModel, overlayType: PlacementOverlayType
    ) {
        val popupDialog = PopupDialog(
            popupPlacementModel,
            overlayType,
            alertHandler = alertHandler,
            logger = logger,
            commonUtils = commonUtils,
            apiClient = apiClient,
            htmlContentParser = htmlContentParser,
            callback = callback
        )
        configurePopupPresentation(popupDialog)
    }

    private fun configurePopupPresentation(popupDialog: PopupDialog) {
        callback(BreadPartnerEvent.RenderPopupView(view = popupDialog))
    }

    private fun showAlert(title: String, message: String) {
        alertHandler.showAlert(title = title, message = message, showOkButton = true)
    }
}